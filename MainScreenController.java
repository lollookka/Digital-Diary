package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainScreenController {
	
	@FXML
    private Button addButton;
    @FXML
    private Button viewButton;
    @FXML
    private Button recycleBinButton;
   

    @FXML
    private void initialize() {
        addButton.setOnAction(event -> addEntry());
        viewButton.setOnAction(event -> viewEntries());
        recycleBinButton.setOnAction(event -> recycleBin());
    }
    
    private void loadScreen(String fxmlPath) {
        try {
        	Scene newScene = SceneLoader.load(fxmlPath);
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(newScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); 
            showError("Failed to load the screen: " + fxmlPath);
        }
    }

    private void addEntry() {
        loadScreen("AddEntryScreen.fxml");
    }

    private void viewEntries() {
        loadScreen("ViewEntriesScreen.fxml");
    }

    private void recycleBin() {
        loadScreen("RecycleBinScreen.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
