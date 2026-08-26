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

public class FuncionarioService {

    private static final Logger LOGGER = Logger.getLogger(FuncionarioService.class.getName());

    public Funcionario buscar(int id) {
        String sql = "SELECT id, nome FROM funcionario WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Funcionario(rs.getInt("id"), rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar funcionario", e);
            throw new InternalErrorException("Erro ao buscar funcionario.");
        }
        return null;
    }

    public Funcionario buscarOuFalhar(int id) {
        Funcionario funcionario = buscar(id);
        if (funcionario == null) {
            throw new NotFoundException("Funcionario #" + id + " nao encontrado.");
        }
        return funcionario;
    }
}
