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

public class ReposicaoService {

    private static final Logger LOGGER = Logger.getLogger(ReposicaoService.class.getName());

    public void registrar(Produto produto, Ponto ponto, Funcionario funcionario, int quantidade) {
        String sql = "INSERT INTO reposicao (produto_id, ponto_id, funcionario_id, quantidade, data_hora) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produto.getId());
            stmt.setInt(2, ponto.getId());
            stmt.setInt(3, funcionario.getId());
            stmt.setInt(4, quantidade);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
            LOGGER.info(() -> "Reposicao registrada: produto=" + produto.getId() + " ponto=" + ponto.getId() + " qtd=" + quantidade);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar reposicao", e);
            throw new InternalErrorException("Erro ao registrar reposicao.");
        }
    }

    public ArrayList<Reposicao> getTodos() {
        ArrayList<Reposicao> lista = new ArrayList<>();
        String sql = """
                SELECT r.*, p.nome as produto_nome, pt.nome as ponto_nome, f.nome as funcionario_nome
                FROM reposicao r
                JOIN produto p ON r.produto_id = p.id
                JOIN ponto pt ON r.ponto_id = pt.id
                JOIN funcionario f ON r.funcionario_id = f.id
                ORDER BY r.id DESC
                """;
        try (Connection conn = DatabaseConnection.getConnection();
                java.sql.Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produto produto = new Produto(rs.getInt("produto_id"), rs.getString("produto_nome"));
                Ponto ponto = new Ponto(rs.getInt("ponto_id"), rs.getString("ponto_nome"));
                Funcionario funcionario = new Funcionario(rs.getInt("funcionario_id"), rs.getString("funcionario_nome"));
                lista.add(new Reposicao(
                        rs.getInt("id"),
                        produto,
                        ponto,
                        funcionario,
                        rs.getInt("quantidade"),
                        rs.getTimestamp("data_hora").toLocalDateTime()));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar reposicoes", e);
            throw new InternalErrorException("Erro ao buscar reposicoes.");
        }
        return lista;
    }
}
