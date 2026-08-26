package estoque;

import estoque.db.DatabaseConnection;
import estoque.exceptions.InternalErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovimentacaoService {

    private static final Logger LOGGER = Logger.getLogger(MovimentacaoService.class.getName());
    private final EstoqueService estoque;

    public MovimentacaoService(EstoqueService estoque) {
        this.estoque = estoque;
    }

    public void registrarSaida(Produto produto, Ponto ponto, Funcionario funcionario, int quantidade) {
        String sql = "INSERT INTO movimentacao (produto_id, ponto_id, funcionario_id, quantidade, tipo, data_hora) VALUES (?, ?, ?, ?, 'SAIDA', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, ponto.getId());
            stmt.setInt(3, funcionario.getId());
            stmt.setInt(4, quantidade);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
            LOGGER.info(() -> "Saida registrada: produto=" + produto.getId() + " ponto=" + ponto.getId() + " qtd=" + quantidade);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar saida", e);
            throw new InternalErrorException("Erro ao registrar saida.");
        }
    }

    public ArrayList<Movimentacao> getTodasSaidas() {
        ArrayList<Movimentacao> lista = new ArrayList<>();
        String sql = """
                SELECT m.*, p.nome as produto_nome, pt.nome as ponto_nome, f.nome as funcionario_nome
                FROM movimentacao m
                JOIN produto p ON m.produto_id = p.id
                JOIN ponto pt ON m.ponto_id = pt.id
                JOIN funcionario f ON m.funcionario_id = f.id
                WHERE m.tipo = 'SAIDA'
                ORDER BY m.id DESC
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                java.sql.Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto(rs.getInt("produto_id"), rs.getString("produto_nome"));
                Ponto ponto = new Ponto(rs.getInt("ponto_id"), rs.getString("ponto_nome"));
                Funcionario funcionario = new Funcionario(rs.getInt("funcionario_id"), rs.getString("funcionario_nome"));
                lista.add(new Movimentacao(
                        rs.getInt("id"),
                        produto,
                        ponto,
                        funcionario,
                        rs.getInt("quantidade"),
                        rs.getTimestamp("data_hora").toLocalDateTime(),
                        TipoMovimentacao.SAIDA));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar saidas", e);
            throw new InternalErrorException("Erro ao buscar saidas.");
        }
        return lista;
    }
}
