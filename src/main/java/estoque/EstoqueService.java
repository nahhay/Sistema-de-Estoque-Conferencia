package estoque;

import estoque.db.DatabaseConnection;
import estoque.exceptions.InternalErrorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EstoqueService {

    private static final Logger LOGGER = Logger.getLogger(EstoqueService.class.getName());
    public int consultarQuantidade(Produto produto, Ponto ponto) {
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
            LOGGER.log(Level.SEVERE, "Erro ao consultar estoque", e);
            throw new InternalErrorException("Erro ao consultar estoque.");
        }
        return 0;
    }
}
