package estoque;

import estoque.db.DatabaseConnection;
import estoque.db.SqlUtils;
import estoque.exceptions.BadRequestException;
import estoque.exceptions.InternalErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConferenciaService {

    private static final Logger LOGGER = Logger.getLogger(ConferenciaService.class.getName());
    private final EstoqueService estoque;

    public ConferenciaService(EstoqueService estoque) {
        this.estoque = estoque;
    }

    public void realizarConferencia(Ponto ponto, Funcionario funcionario, Produto produto, int quantidadeFisica) {
        if (ponto == null || funcionario == null || produto == null) {
            throw new BadRequestException("Ponto, funcionario ou produto invalido.");
        }
        if (quantidadeFisica < 0) {
            throw new BadRequestException("Quantidade fisica invalida.");
        }

        int estoqueInicial = buscarEstoqueInicial(produto, ponto);
        int reposicoes = buscarReposicoes(produto, ponto);
        int saidas = buscarSaidas(produto, ponto);

        int estoqueEsperado = estoqueInicial + reposicoes - saidas;
        int divergencia = quantidadeFisica - estoqueEsperado;

        String sql = """
                INSERT INTO conferencia (produto_id, ponto_id, funcionario_id, estoque_inicial,
                                          reposicoes_recebidas, saidas_realizadas, estoque_esperado,
                                          estoque_fisico, divergencia, data)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, ponto.getId());
            stmt.setInt(3, funcionario.getId());
            stmt.setInt(4, estoqueInicial);
            stmt.setInt(5, reposicoes);
            stmt.setInt(6, saidas);
            stmt.setInt(7, estoqueEsperado);
            stmt.setInt(8, quantidadeFisica);
            stmt.setInt(9, divergencia);
            stmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    LOGGER.info(() -> "Conferencia #" + id + " realizada (divergencia=" + divergencia + ").");
                }
            }
        } catch (SQLException e) {
            if (SqlUtils.isIntegrityViolation(e)) {
                throw new BadRequestException("Produto, ponto ou funcionario informado nao existe.");
            }
            LOGGER.log(Level.SEVERE, "Erro ao realizar conferencia", e);
            throw new InternalErrorException("Erro ao realizar conferencia.");
        }
    }
    public ArrayList<Conferencia> getTodos() {
        ArrayList<Conferencia> lista = new ArrayList<>();
        String sql = """
                SELECT c.*, p.nome as produto_nome, pt.nome as ponto_nome, f.nome as funcionario_nome
                FROM conferencia c
                JOIN produto p ON c.produto_id = p.id
                JOIN ponto pt ON c.ponto_id = pt.id
                JOIN funcionario f ON c.funcionario_id = f.id
                ORDER BY c.id DESC
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ponto ponto = new Ponto(rs.getInt("ponto_id"), rs.getString("ponto_nome"));
                Funcionario funcionario = new Funcionario(rs.getInt("funcionario_id"), rs.getString("funcionario_nome"));
                Produto produto = new Produto(rs.getInt("produto_id"), rs.getString("produto_nome"));

                lista.add(new Conferencia(
                        rs.getInt("id"),
                        ponto,
                        funcionario,
                        produto,
                        rs.getInt("estoque_inicial"),
                        rs.getInt("reposicoes_recebidas"),
                        rs.getInt("saidas_realizadas"),
                        rs.getInt("estoque_esperado"),
                        rs.getInt("estoque_fisico"),
                        rs.getTimestamp("data").toLocalDateTime()));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar conferencias", e);
            throw new InternalErrorException("Erro ao buscar conferencias.");
        }
        return lista;
    }

    private int buscarEstoqueInicial(Produto produto, Ponto ponto) {
        String sql = "SELECT quantidade FROM estoque WHERE produto_id = ? AND ponto_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, ponto.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantidade");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar estoque inicial", e);
        }
        return 0;
    }

    private int buscarReposicoes(Produto produto, Ponto ponto) {
        String sql = "SELECT COALESCE(SUM(quantidade), 0) FROM reposicao WHERE produto_id = ? AND ponto_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, ponto.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar reposicoes", e);
        }
        return 0;
    }

    private int buscarSaidas(Produto produto, Ponto ponto) {
        String sql = "SELECT COALESCE(SUM(quantidade), 0) FROM movimentacao WHERE produto_id = ? AND ponto_id = ? AND tipo = 'SAIDA'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, ponto.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar saidas", e);
        }
        return 0;
    }
}