package rs.autoservice.server;

/**
 * Glavna ulazna tačka za pokretanje backend servera aplikacije.
 *
 * @author Vukasin Miljkovic
 */
public class AppServer {

    public static void main(String[] args) {
        int port = Server.DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        Server server = new Server(port);
        server.start();
    }
}
