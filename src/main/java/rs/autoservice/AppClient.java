package rs.autoservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rs.autoservice.client.NetworkClient;

/**
 * Glavna ulazna tačka za JavaFX klijentsku aplikaciju.
 *
 * @author Vukasin Miljkovic
 */
public class AppClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Pokušavamo inicijalnu konekciju sa serverom
        boolean connected = NetworkClient.getInstance().connect("localhost", 8888);
        if (!connected) {
            System.err.println("[AppClient] Upozorenje: Server trenutno nije dostupan na localhost:8888. Pokrenite AppServer!");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Auto Servis Management System – Prijava na sistem");
        primaryStage.setScene(new Scene(root, 480, 520));
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();

        primaryStage.setOnCloseRequest(event -> {
            NetworkClient.getInstance().disconnect();
            System.exit(0);
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
