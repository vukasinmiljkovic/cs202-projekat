package rs.autoservice.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Pomoćna klasa zadužena za automatsku inicijalizaciju šeme baze podataka
 * i popunjavanje početnim testnim podacima (seed data).
 * Podržava i MySQL (XAMPP) i SQLite.
 *
 * @author Vukasin Miljkovic
 */
public class DatabaseInitializer {

    /**
     * Kreira bazu i sve tabele, te unosi početne test podatke.
     */
    public static void initializeDatabase() {
        if (DatabaseConnection.USE_MYSQL) {
            boolean success = tryInitializeMySQL();
            if (!success) {
                System.out.println("[DatabaseInitializer] Inicijalizujem lokalnu SQLite bazu kao rezervu...");
                initializeSQLite();
            }
        } else {
            initializeSQLite();
        }
    }

    private static boolean tryInitializeMySQL() {
        String serverUrl = "jdbc:mysql://" + DatabaseConnection.MYSQL_HOST +
                "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1. Kreiramo bazu ako ne postoji
            try (Connection serverConn = DriverManager.getConnection(serverUrl, DatabaseConnection.MYSQL_USER, DatabaseConnection.MYSQL_PASSWORD);
                 Statement stmt = serverConn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS `" + DatabaseConnection.MYSQL_DATABASE + "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            }

            // 2. Kreiramo tabele
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement()) {

                // Users
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS `users` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `username` VARCHAR(50) NOT NULL UNIQUE,
                        `password` VARCHAR(255) NOT NULL,
                        `role` VARCHAR(20) NOT NULL,
                        `full_name` VARCHAR(100) NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                // Customers
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS `customers` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `first_name` VARCHAR(50) NOT NULL,
                        `last_name` VARCHAR(50) NOT NULL,
                        `phone` VARCHAR(30) NOT NULL,
                        `email` VARCHAR(100) NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                // Vehicles
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS `vehicles` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `customer_id` INT NOT NULL,
                        `brand` VARCHAR(50) NOT NULL,
                        `model` VARCHAR(50) NOT NULL,
                        `year` INT NOT NULL,
                        `license_plate` VARCHAR(20) NOT NULL,
                        CONSTRAINT `fk_vehicles_customer` FOREIGN KEY (`customer_id`) 
                            REFERENCES `customers` (`id`) ON DELETE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                // Employees
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS `employees` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `first_name` VARCHAR(50) NOT NULL,
                        `last_name` VARCHAR(50) NOT NULL,
                        `position` VARCHAR(100) NOT NULL,
                        `phone` VARCHAR(30) NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                // Services
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS `services` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `name` VARCHAR(100) NOT NULL,
                        `description` TEXT,
                        `price` DOUBLE NOT NULL,
                        `duration_minutes` INT NOT NULL
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                // Appointments
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS `appointments` (
                        `id` INT AUTO_INCREMENT PRIMARY KEY,
                        `customer_id` INT NOT NULL,
                        `vehicle_id` INT NOT NULL,
                        `employee_id` INT NOT NULL,
                        `service_id` INT NOT NULL,
                        `appointment_date` VARCHAR(20) NOT NULL,
                        `appointment_time` VARCHAR(10) NOT NULL,
                        `status` VARCHAR(20) NOT NULL DEFAULT 'ZAKAZANO',
                        `notes` TEXT,
                        `total_price` DOUBLE NOT NULL,
                        CONSTRAINT `fk_appointments_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
                        CONSTRAINT `fk_appointments_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`id`),
                        CONSTRAINT `fk_appointments_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`),
                        CONSTRAINT `fk_appointments_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """);

                seedDataIfEmpty(conn);
                System.out.println("[DatabaseInitializer] XAMPP MySQL baza '" + DatabaseConnection.MYSQL_DATABASE + "' je aktivna i uspešno povezana!");
                return true;
            }

        } catch (Exception e) {
            System.out.println("[DatabaseInitializer] Nije moguće povezivanje na XAMPP MySQL (" + e.getMessage() + ")");
            return false;
        }
    }

    private static void initializeSQLite() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    full_name TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    email TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS vehicles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER NOT NULL,
                    brand TEXT NOT NULL,
                    model TEXT NOT NULL,
                    year INTEGER NOT NULL,
                    license_plate TEXT NOT NULL,
                    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS employees (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    position TEXT NOT NULL,
                    phone TEXT NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS services (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL,
                    duration_minutes INTEGER NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS appointments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER NOT NULL,
                    vehicle_id INTEGER NOT NULL,
                    employee_id INTEGER NOT NULL,
                    service_id INTEGER NOT NULL,
                    appointment_date TEXT NOT NULL,
                    appointment_time TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'ZAKAZANO',
                    notes TEXT,
                    total_price REAL NOT NULL,
                    FOREIGN KEY (customer_id) REFERENCES customers(id),
                    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
                    FOREIGN KEY (employee_id) REFERENCES employees(id),
                    FOREIGN KEY (service_id) REFERENCES services(id)
                );
            """);

            seedDataIfEmpty(conn);
            System.out.println("[DatabaseInitializer] SQLite baza podataka je uspešno inicijalizovana!");

        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] Greška pri inicijalizaciji SQLite baze: " + e.getMessage());
        }
    }

    private static void seedDataIfEmpty(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users;")) {

            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("[DatabaseInitializer] Unos početnih testnih podataka u bazu...");

                // 1. Users
                stmt.execute("""
                    INSERT INTO users (username, password, role, full_name) VALUES
                    ('admin', 'admin123', 'ADMIN', 'Administrator Sistema'),
                    ('radnik', 'radnik123', 'EMPLOYEE', 'Petar Marković');
                """);

                // 2. Customers
                stmt.execute("""
                    INSERT INTO customers (first_name, last_name, phone, email) VALUES
                    ('Marko', 'Petrović', '+381641112233', 'marko.petrovic@gmail.com'),
                    ('Nikola', 'Jovanović', '+381652223344', 'nikola.jovanovic@yahoo.com'),
                    ('Ana', 'Ilić', '+381633334455', 'ana.ilic@gmail.com'),
                    ('Stefan', 'Popović', '+381604445566', 'stefan.popovic@outlook.com'),
                    ('Jelena', 'Nikolić', '+381615556677', 'jelena.nikolic@gmail.com');
                """);

                // 3. Vehicles
                stmt.execute("""
                    INSERT INTO vehicles (customer_id, brand, model, year, license_plate) VALUES
                    (1, 'Volkswagen', 'Golf 7 2.0 TDI', 2017, 'BG-123-AA'),
                    (2, 'BMW', '320d xDrive', 2019, 'NS-456-BB'),
                    (3, 'Audi', 'A4 2.0 TFSI', 2018, 'NI-789-CC'),
                    (4, 'Škoda', 'Octavia 1.6 TDI', 2020, 'KG-321-DD'),
                    (5, 'Renault', 'Megane 1.5 dCi', 2016, 'BG-654-EE');
                """);

                // 4. Employees
                stmt.execute("""
                    INSERT INTO employees (first_name, last_name, position, phone) VALUES
                    ('Petar', 'Marković', 'Glavni automehaničar', '+381648881122'),
                    ('Milan', 'Đorđević', 'Auto-električar', '+381658882233'),
                    ('Dejan', 'Stanković', 'Dijagnostičar', '+381638883344'),
                    ('Nemanja', 'Simić', 'Vulkanizer i trap', '+381628884455');
                """);

                // 5. Services
                stmt.execute("""
                    INSERT INTO services (name, description, price, duration_minutes) VALUES
                    ('Mali servis', 'Zamena motornog ulja, filtera ulja, vazduha, goriva i kabine', 9500.0, 60),
                    ('Veliki servis', 'Zamena zupčastog kaiša, španera, rolera i vodene pumpe', 38000.0, 180),
                    ('Kompjuterska dijagnostika', 'Kompletno očitavanje grešaka svih elektronskih modula', 3000.0, 30),
                    ('Servis kočionog sistema', 'Zamena prednjih/zadnjih kočionih pločica i diskova sa proverom ulja', 14000.0, 90),
                    ('Montaža i balansiranje guma', 'Demontaža, montaža i balansiranje seta od 4 pneumatika', 4000.0, 45);
                """);

                // 6. Appointments
                stmt.execute("""
                    INSERT INTO appointments (customer_id, vehicle_id, employee_id, service_id, appointment_date, appointment_time, status, notes, total_price) VALUES
                    (1, 1, 1, 1, '2026-08-10', '09:00', 'ZAVRŠENO', 'Redovan servis na 150.000 km, sipano Castrol 5W30', 9500.0),
                    (2, 2, 3, 3, '2026-08-12', '11:30', 'ZAVRŠENO', 'Otklonjena greška na EGR ventilu', 3000.0),
                    (3, 3, 2, 2, '2026-08-20', '10:00', 'ZAKAZANO', 'Zameniti i PK kaiš i remenicu alternatora', 38000.0),
                    (4, 4, 1, 4, '2026-08-22', '14:00', 'ZAKAZANO', 'Klijent prijavio škripanje pri kočenju', 14000.0),
                    (5, 5, 4, 5, '2026-08-15', '16:00', 'OTKAZANO', 'Klijent otkazao zbog službenog puta', 4000.0);
                """);
            }
        }
    }
}
