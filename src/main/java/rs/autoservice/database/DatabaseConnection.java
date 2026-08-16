package rs.autoservice.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Upravlja konekcijom sa bazom podataka.
 * Podržava i MySQL (preko XAMPP-a) i SQLite.
 *
 * Ako je XAMPP MySQL pokrenut na localhost:3306, koristi MySQL.
 * Ako XAMPP nije startovan, automatski prelazi na lokalni SQLite fajl tako da aplikacija uvek radi!
 *
 * @author Vukasin Miljkovic
 */
public class DatabaseConnection {

    /**
     * Da li se preferira XAMPP MySQL baza.
     */
    public static boolean USE_MYSQL = true;

    // --- MySQL (XAMPP) Konfiguracija ---
    public static final String MYSQL_HOST = "localhost:3306";
    public static final String MYSQL_DATABASE = "autoservice";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PASSWORD = "";

    public static final String MYSQL_URL = "jdbc:mysql://" + MYSQL_HOST + "/" + MYSQL_DATABASE +
            "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    // --- SQLite Konfiguracija ---
    private static final String SQLITE_DIR = "database";
    private static final String SQLITE_FILE = "database/autoservice.db";
    private static final String SQLITE_URL = "jdbc:sqlite:" + SQLITE_FILE;

    private static Connection connection = null;
    private static boolean activeIsMySQL = false;

    /**
     * Vraća aktivnu JDBC konekciju.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (USE_MYSQL) {
                try {
                    connection = getMySQLConnection();
                    activeIsMySQL = true;
                } catch (Exception e) {
                    System.out.println("[DatabaseConnection] XAMPP MySQL nije pokrenut na portu 3306. Prebacujem se na lokalnu SQLite bazu.");
                    connection = getSQLiteConnection();
                    activeIsMySQL = false;
                }
            } else {
                connection = getSQLiteConnection();
                activeIsMySQL = false;
            }
        }
        return connection;
    }

    public static boolean isUsingMySQL() {
        return activeIsMySQL;
    }

    /**
     * Otvara konekciju prema MySQL (XAMPP) bazi.
     */
    private static Connection getMySQLConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC drajver nije pronađen!", e);
        }
    }

    /**
     * Otvara konekciju prema lokalnoj SQLite bazi.
     */
    private static Connection getSQLiteConnection() throws SQLException {
        File dir = new File(SQLITE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(SQLITE_URL);
            try (var stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC drajver nije pronađen!", e);
        }
    }

    /**
     * Zatvara konekciju sa bazom podataka.
     */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Greška prilikom zatvaranja konekcije: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
