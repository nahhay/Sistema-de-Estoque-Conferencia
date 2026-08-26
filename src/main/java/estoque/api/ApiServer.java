package estoque.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import estoque.Conferencia;
import estoque.ConferenciaService;
import estoque.EstoqueService;
import estoque.Funcionario;
import estoque.FuncionarioService;
import estoque.Ponto;
import estoque.PontoService;
import estoque.Produto;
import estoque.ProdutoService;
import estoque.Reposicao;
import estoque.ReposicaoService;
import estoque.Movimentacao;
import estoque.MovimentacaoService;
import estoque.api.requests.ConferenciaRequest;
import estoque.api.requests.MovimentacaoRequest;
import estoque.api.requests.ReposicaoRequest;
import estoque.db.DatabaseConnection;
import estoque.exceptions.BadRequestException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ApiServer {

    private static final Logger LOGGER = Logger.getLogger(ApiServer.class.getName());
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

    private static final ProdutoService produtoService = new ProdutoService();
    private static final FuncionarioService funcionarioService = new FuncionarioService();
    private static final PontoService pontoService = new PontoService();
    private static final EstoqueService estoqueService = new EstoqueService();
    private static final ConferenciaService conferenciaService = new ConferenciaService(estoqueService);
    private static final ReposicaoService reposicaoService = new ReposicaoService();
    private static final MovimentacaoService movimentacaoService = new MovimentacaoService(estoqueService);

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/api/conferencias", new ConferenciasHandler());
        server.createContext("/api/estoque", new EstoqueHandler());
        server.createContext("/api/reposicoes", new ReposicoesHandler());
        server.createContext("/api/movimentacoes", new MovimentacoesHandler());

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(16));
        server.start();

        LOGGER.info("Servidor API rodando em http://localhost:" + PORT);
        LOGGER.info("Acesse o frontend em: frontend/index.html");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Encerrando servidor...");
            server.stop(2);
            DatabaseConnection.shutdown();
        }, "server-shutdown"));
    }

    static class ConferenciasHandler extends BaseHandler {
        @Override
        protected void handleRequest(HttpExchange exchange) throws Exception {
            String method = exchange.getRequestMethod();

            if ("GET".equals(method)) {
                ArrayList<Conferencia> conferencias = conferenciaService.getTodos();
                ArrayList<Map<String, Object>> lista = new ArrayList<>();
                for (Conferencia c : conferencias) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", c.getId());
                    item.put("produtoId", c.getProduto().getId());
                    item.put("produtoNome", c.getProduto().getNome());
                    item.put("pontoId", c.getPonto().getId());
                    item.put("pontoNome", c.getPonto().getNome());
                    item.put("funcionarioId", c.getResponsavel().getId());
                    item.put("funcionarioNome", c.getResponsavel().getNome());
                    item.put("estoqueInicial", c.getEstoqueInicial());
                    item.put("reposicoesRecebidas", c.getReposicoesRecebidas());
                    item.put("saidasRealizadas", c.getSaidasRealizadas());
                    item.put("estoqueEsperado", c.getEstoqueEsperado());
                    item.put("estoqueFisico", c.getEstoqueFisico());
                    item.put("divergencia", c.getDivergencia());
                    item.put("data", c.getData().toString());
                    lista.add(item);
                }
                sendJson(exchange, 200, ApiResponse.ok(lista));
            } else if ("POST".equals(method)) {
                ConferenciaRequest request = parseBody(exchange, ConferenciaRequest.class);
                if (request.getQuantidadeFisica() < 0) {
                    throw new BadRequestException("Quantidade fisica invalida.");
                }

                Ponto ponto = pontoService.buscarOuFalhar(request.getPontoId());
                Funcionario funcionario = funcionarioService.buscarOuFalhar(request.getFuncionarioId());
                Produto produto = produtoService.buscarOuFalhar(request.getProdutoId());

                conferenciaService.realizarConferencia(ponto, funcionario, produto, request.getQuantidadeFisica());
                sendJson(exchange, 201, ApiResponse.mensagem("Conferencia realizada com sucesso!"));
            } else {
                sendJson(exchange, 405, ApiResponse.erro("Metodo nao permitido."));
            }
        }
    }

    static class EstoqueHandler extends BaseHandler {
        @Override
        protected void handleRequest(HttpExchange exchange) throws Exception {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendJson(exchange, 405, ApiResponse.erro("Metodo nao permitido."));
                return;
            }

            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            int produtoId = parseIntParam(params, "produtoId");
            int pontoId = parseIntParam(params, "pontoId");

            Produto produto = produtoService.buscarOuFalhar(produtoId);
            Ponto ponto = pontoService.buscarOuFalhar(pontoId);

            int quantidade = estoqueService.consultarQuantidade(produto, ponto);
            sendJson(exchange, 200, ApiResponse.ok(quantidade));
        }
    }

    static class ReposicoesHandler extends BaseHandler {
        @Override
        protected void handleRequest(HttpExchange exchange) throws Exception {
            String method = exchange.getRequestMethod();

            if ("GET".equals(method)) {
                ArrayList<Reposicao> reposicoes = reposicaoService.getTodos();
                ArrayList<Map<String, Object>> lista = new ArrayList<>();
                for (Reposicao r : reposicoes) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", r.getId());
                    item.put("produtoId", r.getProduto().getId());
                    item.put("produtoNome", r.getProduto().getNome());
                    item.put("pontoId", r.getPonto().getId());
                    item.put("pontoNome", r.getPonto().getNome());
                    item.put("funcionarioId", r.getFuncionario().getId());
                    item.put("funcionarioNome", r.getFuncionario().getNome());
                    item.put("quantidade", r.getQuantidade());
                    item.put("dataHora", r.getDataHora().toString());
                    lista.add(item);
                }
                sendJson(exchange, 200, ApiResponse.ok(lista));
            } else if ("POST".equals(method)) {
                ReposicaoRequest request = parseBody(exchange, ReposicaoRequest.class);
                if (request.getQuantidade() <= 0) {
                    throw new BadRequestException("Quantidade deve ser positiva.");
                }

                Ponto ponto = pontoService.buscarOuFalhar(request.getPontoId());
                Funcionario funcionario = funcionarioService.buscarOuFalhar(request.getFuncionarioId());
                Produto produto = produtoService.buscarOuFalhar(request.getProdutoId());

                reposicaoService.registrar(produto, ponto, funcionario, request.getQuantidade());
                sendJson(exchange, 201, ApiResponse.mensagem("Reposicao registrada com sucesso!"));
            } else {
                sendJson(exchange, 405, ApiResponse.erro("Metodo nao permitido."));
            }
        }
    }

    static class MovimentacoesHandler extends BaseHandler {
        @Override
        protected void handleRequest(HttpExchange exchange) throws Exception {
            String method = exchange.getRequestMethod();

            if ("GET".equals(method)) {
                ArrayList<Movimentacao> movimentacoes = movimentacaoService.getTodasSaidas();
                ArrayList<Map<String, Object>> lista = new ArrayList<>();
                for (Movimentacao m : movimentacoes) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", m.getId());
                    item.put("produtoId", m.getProduto().getId());
                    item.put("produtoNome", m.getProduto().getNome());
                    item.put("pontoId", m.getPonto().getId());
                    item.put("pontoNome", m.getPonto().getNome());
                    item.put("funcionarioId", m.getFuncionario().getId());
                    item.put("funcionarioNome", m.getFuncionario().getNome());
                    item.put("quantidade", m.getQuantidade());
                    item.put("dataHora", m.getDataHora().toString());
                    lista.add(item);
                }
                sendJson(exchange, 200, ApiResponse.ok(lista));
            } else if ("POST".equals(method)) {
                MovimentacaoRequest request = parseBody(exchange, MovimentacaoRequest.class);
                if (request.getQuantidade() <= 0) {
                    throw new BadRequestException("Quantidade deve ser positiva.");
                }

                Ponto ponto = pontoService.buscarOuFalhar(request.getPontoId());
                Funcionario funcionario = funcionarioService.buscarOuFalhar(request.getFuncionarioId());
                Produto produto = produtoService.buscarOuFalhar(request.getProdutoId());

                movimentacaoService.registrarSaida(produto, ponto, funcionario, request.getQuantidade());
                sendJson(exchange, 201, ApiResponse.mensagem("Saida registrada com sucesso!"));
            } else {
                sendJson(exchange, 405, ApiResponse.erro("Metodo nao permitido."));
            }
        }
    }
}