package rs.autoservice.dao;

import rs.autoservice.database.DatabaseConnection;
import rs.autoservice.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) klasa za entitet {@link Customer}.
 * Izvršava SQL operacije (CRUD) nad tabelom 'customers'.
 *
 * @author Vukasin Miljkovic
 */
public class CustomerDAO {

    /**
     * Vraća listu svih klijenata iz baze.
     *
     * @return Lista svih objekata {@link Customer}
     * @throws SQLException U slučaju greške pri radu sa bazom
     */
    public List<Customer> getAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY last_name, first_name ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToCustomer(rs));
            }
        }
        return list;
    }

    /**
     * Pronalazi klijenta po ID-ju.
     *
     * @param id ID klijenta
     * @return Pronađeni klijent ili null ako ne postoji
     * @throws SQLException U slučaju greške
     */
    public Customer getById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        }
        return null;
    }

    /**
     * Unosi novog klijenta u bazu.
     *
     * @param customer Objekat klijenta za unos
     * @return Generisani ID unetog klijenta ili -1 ako unos nije uspeo
     * @throws SQLException U slučaju greške
     */
    public int insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (first_name, last_name, phone, email) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setId(generatedKeys.getInt(1));
                        return customer.getId();
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Ažurira podatke postojećeg klijenta.
     *
     * @param customer Klijent sa izmenjenim podacima
     * @return true ako je ažuriranje uspelo, false inače
     * @throws SQLException U slučaju greške
     */
    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, phone = ?, email = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
            pstmt.setInt(5, customer.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Briše klijenta iz baze na osnovu ID-ja.
     *
     * @param id ID klijenta
     * @return true ako je obrisan, false inače
     * @throws SQLException U slučaju greške
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Mapira red iz ResultSet-a u objekat {@link Customer}.
     */
    private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("email")
        );
    }
}
