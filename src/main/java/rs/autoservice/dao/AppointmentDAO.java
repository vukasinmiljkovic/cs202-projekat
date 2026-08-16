package rs.autoservice.dao;

import rs.autoservice.database.DatabaseConnection;
import rs.autoservice.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) za entitet {@link Appointment}.
 * Izvršava SQL operacije nad tabelom 'appointments' uz povezivanje sa relacijama.
 *
 * @author Vukasin Miljkovic
 */
public class AppointmentDAO {

    private static final String BASE_SELECT = """
        SELECT a.id, a.customer_id, a.vehicle_id, a.employee_id, a.service_id,
               a.appointment_date, a.appointment_time, a.status, a.notes, a.total_price,
               c.first_name AS cust_first_name, c.last_name AS cust_last_name,
               v.brand AS veh_brand, v.model AS veh_model, v.license_plate AS veh_plate,
               e.first_name AS emp_first_name, e.last_name AS emp_last_name,
               s.name AS service_name
        FROM appointments a
        LEFT JOIN customers c ON a.customer_id = c.id
        LEFT JOIN vehicles v ON a.vehicle_id = v.id
        LEFT JOIN employees e ON a.employee_id = e.id
        LEFT JOIN services s ON a.service_id = s.id
    """;

    /**
     * Vraća sve termine sortirane po datumu i vremenu.
     */
    public List<Appointment> getAll() throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY a.appointment_date DESC, a.appointment_time ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToAppointment(rs));
            }
        }
        return list;
    }

    /**
     * Vraća termine za izabrani datum.
     */
    public List<Appointment> getByDate(String date) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE a.appointment_date = ? ORDER BY a.appointment_time ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Vraća sve termine određenog zaposlenog na određeni datum (koristi se za proveru preklapanja).
     */
    public List<Appointment> getByEmployeeAndDate(int employeeId, String date) throws SQLException {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE a.employee_id = ? AND a.appointment_date = ? AND a.status != 'OTKAZANO' ORDER BY a.appointment_time ASC;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            pstmt.setString(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAppointment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Vraća termin po ID-ju.
     */
    public Appointment getById(int id) throws SQLException {
        String sql = BASE_SELECT + " WHERE a.id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAppointment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Unosi novi termin u bazu.
     */
    public int insert(Appointment appointment) throws SQLException {
        String sql = """
            INSERT INTO appointments 
            (customer_id, vehicle_id, employee_id, service_id, appointment_date, appointment_time, status, notes, total_price) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, appointment.getCustomerId());
            pstmt.setInt(2, appointment.getVehicleId());
            pstmt.setInt(3, appointment.getEmployeeId());
            pstmt.setInt(4, appointment.getServiceId());
            pstmt.setString(5, appointment.getAppointmentDate());
            pstmt.setString(6, appointment.getAppointmentTime());
            pstmt.setString(7, appointment.getStatus());
            pstmt.setString(8, appointment.getNotes());
            pstmt.setDouble(9, appointment.getTotalPrice());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1));
                        return appointment.getId();
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Ažurira postojeći termin.
     */
    public boolean update(Appointment appointment) throws SQLException {
        String sql = """
            UPDATE appointments 
            SET customer_id = ?, vehicle_id = ?, employee_id = ?, service_id = ?,
                appointment_date = ?, appointment_time = ?, status = ?, notes = ?, total_price = ?
            WHERE id = ?;
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointment.getCustomerId());
            pstmt.setInt(2, appointment.getVehicleId());
            pstmt.setInt(3, appointment.getEmployeeId());
            pstmt.setInt(4, appointment.getServiceId());
            pstmt.setString(5, appointment.getAppointmentDate());
            pstmt.setString(6, appointment.getAppointmentTime());
            pstmt.setString(7, appointment.getStatus());
            pstmt.setString(8, appointment.getNotes());
            pstmt.setDouble(9, appointment.getTotalPrice());
            pstmt.setInt(10, appointment.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Menja samo status termina (npr. ZAVRŠENO, OTKAZANO).
     */
    public boolean updateStatus(int id, String newStatus) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Briše termin iz baze.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM appointments WHERE id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Računa statistiku poslovanja: ukupan broj, po statusima i ukupan prihod.
     */
    public Map<String, Object> getStatistics() throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        String sql = """
            SELECT 
                COUNT(*) AS total_appointments,
                SUM(CASE WHEN status = 'ZAVRŠENO' THEN 1 ELSE 0 END) AS completed_count,
                SUM(CASE WHEN status = 'ZAKAZANO' THEN 1 ELSE 0 END) AS scheduled_count,
                SUM(CASE WHEN status = 'OTKAZANO' THEN 1 ELSE 0 END) AS cancelled_count,
                SUM(CASE WHEN status = 'ZAVRŠENO' THEN total_price ELSE 0 END) AS total_revenue
            FROM appointments;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                stats.put("total", rs.getInt("total_appointments"));
                stats.put("completed", rs.getInt("completed_count"));
                stats.put("scheduled", rs.getInt("scheduled_count"));
                stats.put("cancelled", rs.getInt("cancelled_count"));
                stats.put("revenue", rs.getDouble("total_revenue"));
            }
        }

        // Popularnost usluga
        String popularSql = """
            SELECT s.name, COUNT(a.id) AS usage_count
            FROM appointments a
            JOIN services s ON a.service_id = s.id
            GROUP BY s.id, s.name
            ORDER BY usage_count DESC;
        """;
        Map<String, Integer> serviceCounts = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(popularSql)) {

            while (rs.next()) {
                serviceCounts.put(rs.getString("name"), rs.getInt("usage_count"));
            }
        }
        stats.put("serviceCounts", serviceCounts);

        return stats;
    }

    private Appointment mapRowToAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment(
                rs.getInt("id"),
                rs.getInt("customer_id"),
                rs.getInt("vehicle_id"),
                rs.getInt("employee_id"),
                rs.getInt("service_id"),
                rs.getString("appointment_date"),
                rs.getString("appointment_time"),
                rs.getString("status"),
                rs.getString("notes"),
                rs.getDouble("total_price")
        );

        String cFirst = rs.getString("cust_first_name");
        String cLast = rs.getString("cust_last_name");
        a.setCustomerName((cFirst != null && cLast != null) ? (cFirst + " " + cLast) : "N/A");

        String vBrand = rs.getString("veh_brand");
        String vModel = rs.getString("veh_model");
        String vPlate = rs.getString("veh_plate");
        a.setVehicleDetails((vBrand != null) ? (vBrand + " " + vModel + " [" + vPlate + "]") : "N/A");

        String eFirst = rs.getString("emp_first_name");
        String eLast = rs.getString("emp_last_name");
        a.setEmployeeName((eFirst != null && eLast != null) ? (eFirst + " " + eLast) : "N/A");

        String sName = rs.getString("service_name");
        a.setServiceName(sName != null ? sName : "N/A");

        return a;
    }
}
