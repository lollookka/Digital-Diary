package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LoginScreenController {

    @FXML
    private TextField usernameOrEmailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registerLink;  // Add this line to reference the register link in your controller

    @FXML
    public void loginUser() {
        String usernameOrEmail = usernameOrEmailTextField.getText().trim();
        String password = passwordTextField.getText().trim();

        if (usernameOrEmail.isEmpty()) {
            showAlert(AlertType.ERROR, "Please enter either username or email.");
            return;
        }

        if (password.isEmpty()) {
            showAlert(AlertType.ERROR, "Please enter your password.");
            return;
        }

        // Validate login credentials
        if (isLoginValid(usernameOrEmail, password)) {
            showAlert(AlertType.INFORMATION, "Login successful!");
            switchToMainScreen();
        } else {
            showAlert(AlertType.ERROR, "Invalid username/email or password.");
        }
    }

    private boolean isLoginValid(String usernameOrEmail, String password) {
        boolean isValid = false;

        // Check if it's a username or email
        if (usernameOrEmail.contains("@")) {
            isValid = checkEmailLogin(usernameOrEmail, password);
        } else {
            File userFile = new File("data/" + usernameOrEmail + "_details.txt");
            if (userFile.exists()) {
                isValid = checkPassword(userFile, password);
            }
        }

        return isValid;
    }

    private boolean checkEmailLogin(String email, String password) {
        File[] userFiles = new File("data").listFiles((dir, name) -> name.endsWith("_details.txt"));
        if (userFiles != null) {
            for (File userFile : userFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Email:") && line.contains(email)) {
                            return checkPassword(userFile, password);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean checkPassword(File userFile, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Password:") && line.contains(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchToMainScreen() {
        try {
            // Load the main screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Scene mainScene = new Scene(loader.load());

            // Get the current stage and set the new scene
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.setScene(mainScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Failed to load the main screen.");
        }
    }

    // This method handles the "Don't have an account? Register here" link click event
    @FXML
    public void goToRegister() {
        try {
            // Load the registration screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/RegistrationScreen.fxml"));
            Scene registerScene = new Scene(loader.load());

            // Get the current stage and set the new scene
            Stage currentStage = (Stage) registerLink.getScene().getWindow();
            currentStage.setScene(registerScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Failed to load the registration screen.");
        }
    }
}
