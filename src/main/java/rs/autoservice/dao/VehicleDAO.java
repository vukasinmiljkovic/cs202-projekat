package rs.autoservice.dao;

import rs.autoservice.database.DatabaseConnection;
import rs.autoservice.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) za entitet {@link Vehicle}.
 * Izvršava SQL operacije (CRUD) nad tabelom 'vehicles'.
 *
 * @author Vukasin Miljkovic
 */
public class VehicleDAO {

    /**
     * Vraća sva vozila sa povezanim imenom vlasnika (JOIN sa customers).
     *
     * @return Lista objekata {@link Vehicle}
     * @throws SQLException U slučaju greške pri radu sa bazom
     */
    public List<Vehicle> getAll() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = """
            SELECT v.id, v.customer_id, v.brand, v.model, v.year, v.license_plate,
                   c.first_name, c.last_name
            FROM vehicles v
            LEFT JOIN customers c ON v.customer_id = c.id
            ORDER BY v.brand, v.model ASC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToVehicle(rs));
            }
        }
        return list;
    }

    /**
     * Vraća listu vozila koja pripadaju određenom klijentu.
     *
     * @param customerId ID klijenta
     * @return Lista vozila vlasnika
     * @throws SQLException U slučaju greške
     */
    public List<Vehicle> getByCustomerId(int customerId) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = """
            SELECT v.id, v.customer_id, v.brand, v.model, v.year, v.license_plate,
                   c.first_name, c.last_name
            FROM vehicles v
            LEFT JOIN customers c ON v.customer_id = c.id
            WHERE v.customer_id = ?
            ORDER BY v.brand, v.model ASC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToVehicle(rs));
                }
            }
        }
        return list;
    }

    /**
     * Pronalazi vozilo po ID-ju.
     */
    public Vehicle getById(int id) throws SQLException {
        String sql = """
            SELECT v.id, v.customer_id, v.brand, v.model, v.year, v.license_plate,
                   c.first_name, c.last_name
            FROM vehicles v
            LEFT JOIN customers c ON v.customer_id = c.id
            WHERE v.id = ?;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToVehicle(rs);
                }
            }
        }
        return null;
    }

    /**
     * Unosi novo vozilo u bazu.
     */
    public int insert(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (customer_id, brand, model, year, license_plate) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, vehicle.getCustomerId());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setString(5, vehicle.getLicensePlate());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        vehicle.setId(generatedKeys.getInt(1));
                        return vehicle.getId();
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Ažurira podatke postojećeg vozila.
     */
    public boolean update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET customer_id = ?, brand = ?, model = ?, year = ?, license_plate = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vehicle.getCustomerId());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setString(5, vehicle.getLicensePlate());
            pstmt.setInt(6, vehicle.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Briše vozilo iz baze.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Vehicle mapRowToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getString("license_plate")
        );
        String fn = rs.getString("first_name");
        String ln = rs.getString("last_name");
        if (fn != null && ln != null) {
            v.setCustomerName(fn + " " + ln);
        } else {
            v.setCustomerName("Nepoznat vlasnik");
        }
        return v;
    }
}
