package estoque;

import estoque.db.DatabaseConnection;
import estoque.exceptions.InternalErrorException;
import estoque.exceptions.NotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProdutoService {

    private static final Logger LOGGER = Logger.getLogger(ProdutoService.class.getName());
    public Produto buscar(int id) {
        String sql = "SELECT id, nome FROM produto WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produto(rs.getInt("id"), rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar produto", e);
            throw new InternalErrorException("Erro ao buscar produto.");
        }
        return null;
    }

    public Produto buscarOuFalhar(int id) {
        Produto produto = buscar(id);
        if (produto == null) {
            throw new NotFoundException("Produto #" + id + " nao encontrado.");
        }
        return produto;
    }
}
