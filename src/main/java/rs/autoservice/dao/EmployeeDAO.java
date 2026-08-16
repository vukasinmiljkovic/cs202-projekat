package rs.autoservice.dao;

import rs.autoservice.database.DatabaseConnection;
import rs.autoservice.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) za entitet {@link Employee}.
 * Izvršava SQL operacije (CRUD) nad tabelom 'employees'.
 *
 * @author Vukasin Miljkovic
 */
public class EmployeeDAO {

    /**
     * Vraća sve zaposlene iz baze.
     */
    public List<Employee> getAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY last_name, first_name ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToEmployee(rs));
            }
        }
        return list;
    }

    /**
     * Pronalazi zaposlenog po ID-ju.
     */
    public Employee getById(int id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEmployee(rs);
                }
            }
        }
        return null;
    }

    /**
     * Unosi novog zaposlenog u bazu.
     */
    public int insert(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (first_name, last_name, position, phone) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getPosition());
            pstmt.setString(4, employee.getPhone());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setId(generatedKeys.getInt(1));
                        return employee.getId();
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Ažurira podatke postojećeg zaposlenog.
     */
    public boolean update(Employee employee) throws SQLException {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, position = ?, phone = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employee.getFirstName());
            pstmt.setString(2, employee.getLastName());
            pstmt.setString(3, employee.getPosition());
            pstmt.setString(4, employee.getPhone());
            pstmt.setInt(5, employee.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Briše zaposlenog iz baze.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Employee mapRowToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("position"),
                rs.getString("phone")
        );
    }
}
