package rs.autoservice.dao;

import rs.autoservice.database.DatabaseConnection;
import rs.autoservice.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) za entitet {@link User}.
 * Izvršava proveru korisničkih podataka pri prijavi na sistem.
 *
 * @author Vukasin Miljkovic
 */
public class UserDAO {

    /**
     * Proverava korisničko ime i lozinku.
     *
     * @param username Korisničko ime
     * @param password Lozinka
     * @return {@link User} ako su podaci ispravni, null u suprotnom
     * @throws SQLException U slučaju greške pri radu sa bazom
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("full_name")
                    );
                }
            }
        }
        return null;
    }
}
