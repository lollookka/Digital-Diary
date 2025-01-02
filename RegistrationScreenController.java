package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RegistrationScreenController {

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button registerButton;

    @FXML
    public void handleRegisterAction() {
        String username = usernameTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "All fields are required.");
            return;
        }

        // Check if the username already exists
        if (isUserExists(username)) {
            showAlert(AlertType.ERROR, "Username already exists.");
            return;
        }

        // Save user details to file
        saveUserDetails(username, email, password);

        // Show success message
        showAlert(AlertType.INFORMATION, "Registration successful!");

        goToLogin();
        
        // Clear the fields after registration
        usernameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
    }

    private boolean isUserExists(String username) {
        File userFile = new File("data/" + username + "_details.txt");
        return userFile.exists();
    }

    private void saveUserDetails(String username, String email, String password) {
        File userFile = new File("data/" + username + "_details.txt");

        // Ensure the "data" directory exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            writer.write("Username: " + username + "\n");
            writer.write("Email: " + email + "\n");
            writer.write("Password: " + password + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "An error occurred while saving your details.");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Failed to load login screen.");
        }
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
