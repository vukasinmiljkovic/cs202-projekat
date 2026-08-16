package rs.autoservice.dao;

import rs.autoservice.database.DatabaseConnection;
import rs.autoservice.model.ServiceItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) za entitet {@link ServiceItem}.
 * Izvršava SQL operacije (CRUD) nad tabelom 'services'.
 *
 * @author Vukasin Miljkovic
 */
public class ServiceDAO {

    /**
     * Vraća listu svih usluga iz cenovnika.
     */
    public List<ServiceItem> getAll() throws SQLException {
        List<ServiceItem> list = new ArrayList<>();
        String sql = "SELECT * FROM services ORDER BY name ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToService(rs));
            }
        }
        return list;
    }

    /**
     * Pronalazi uslugu po ID-ju.
     */
    public ServiceItem getById(int id) throws SQLException {
        String sql = "SELECT * FROM services WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToService(rs);
                }
            }
        }
        return null;
    }

    /**
     * Unosi novu uslugu u bazu.
     */
    public int insert(ServiceItem service) throws SQLException {
        String sql = "INSERT INTO services (name, description, price, duration_minutes) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setInt(4, service.getDurationMinutes());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        service.setId(generatedKeys.getInt(1));
                        return service.getId();
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Ažurira podatke postojeće usluge.
     */
    public boolean update(ServiceItem service) throws SQLException {
        String sql = "UPDATE services SET name = ?, description = ?, price = ?, duration_minutes = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, service.getName());
            pstmt.setString(2, service.getDescription());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setInt(4, service.getDurationMinutes());
            pstmt.setInt(5, service.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Briše uslugu iz baze.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM services WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private ServiceItem mapRowToService(ResultSet rs) throws SQLException {
        return new ServiceItem(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("duration_minutes")
        );
    }
}
