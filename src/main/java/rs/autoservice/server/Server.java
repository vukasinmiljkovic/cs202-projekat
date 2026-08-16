package rs.autoservice.server;

import rs.autoservice.database.DatabaseInitializer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Glavna TCP Socket serverska komponenta.
 * Sluša dolazne klijentske konekcije i pokreće zasebnu nit za svakog klijenta.
 *
 * @author Vukasin Miljkovic
 */
public class Server {

    public static final int DEFAULT_PORT = 8888;
    private final int port;
    private boolean running = false;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    /**
     * Pokreće server: inicijalizuje bazu podataka i otvara mrežni port.
     */
    public void start() {
        // 1. Inicijalizujemo bazu podataka pre prihvatanja konekcija
        DatabaseInitializer.initializeDatabase();

        running = true;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("==================================================");
            System.out.println("  AUTO SERVIS - BACKEND SERVER POKRENUT");
            System.out.println("  Slušam klijente na portu: " + port);
            System.out.println("==================================================");

            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    new Thread(handler).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("[Server] Greška pri prihvatanju konekcije: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Nije moguće pokrenuti server na portu " + port + ": " + e.getMessage());
        }
    }

    /**
     * Zaustavlja rad servera.
     */
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
