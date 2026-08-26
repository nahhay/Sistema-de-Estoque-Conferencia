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

public class PontoService {

    private static final Logger LOGGER = Logger.getLogger(PontoService.class.getName());
    public Ponto buscar(int id) {
        String sql = "SELECT id, nome FROM ponto WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Ponto(rs.getInt("id"), rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar ponto", e);
            throw new InternalErrorException("Erro ao buscar ponto.");
        }
        return null;
    }

    public Ponto buscarOuFalhar(int id) {
        Ponto ponto = buscar(id);
        if (ponto == null) {
            throw new NotFoundException("Ponto #" + id + " nao encontrado.");
        }
        return ponto;
    }

}
