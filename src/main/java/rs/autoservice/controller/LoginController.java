package rs.autoservice.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rs.autoservice.client.NetworkClient;
import rs.autoservice.model.Response;
import rs.autoservice.model.User;
import rs.autoservice.util.ValidationUtil;

import java.io.IOException;

/**
 * JavaFX kontroler za Login prozor.
 * Upravlja prijavom korisnika preko mrežnog klijenta.
 *
 * @author Vukasin Miljkovic
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final NetworkClient networkClient = NetworkClient.getInstance();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            showError("Molimo unesite korisničko ime i lozinku.");
            return;
        }

        // Šaljemo zahtev serveru
        Response<User> response = networkClient.login(username.trim(), password.trim());

        if (response.isSuccess() && response.getData() != null) {
            openMainDashboard();
        } else {
            showError(response.getMessage() != null ? response.getMessage() : "Neuspešna prijava.");
        }
    }

    @FXML
    private void handleQuickAdmin(ActionEvent event) {
        usernameField.setText("admin");
        passwordField.setText("admin123");
        handleLogin(event);
    }

    @FXML
    private void handleQuickEmployee(ActionEvent event) {
        usernameField.setText("radnik");
        passwordField.setText("radnik123");
        handleLogin(event);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void openMainDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Auto Servis Management System - Glavni Prozor");
            stage.setScene(new Scene(root, 1200, 800));
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Greška pri učitavanju glavnog ekrana: " + e.getMessage());
        }
    }
}
