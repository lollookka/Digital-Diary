package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;

public class RecycleBinScreenController {

    @FXML
    private ListView<DiaryEntry> recycleBinListView;

    @FXML
    private Button restoreButton;
    @FXML
    private Button permanentDeleteButton;
    @FXML
    private Button backButton;

    public void initialize() {
        assert recycleBinListView != null : "recycleBinListView is null";
        assert restoreButton != null : "restoreButton is null";
        assert permanentDeleteButton != null : "permanentDeleteButton is null";
        assert backButton != null : "backButton is null";

        loadRecycleBinEntries();

        recycleBinListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            restoreButton.setDisable(newSelection == null);
            permanentDeleteButton.setDisable(newSelection == null);
        });
    }


    private void loadRecycleBinEntries() {
        // Get entries from the recycle bin (those marked for deletion within the last 30 days)
        recycleBinListView.getItems().setAll(DiaryDataStore.getRecycleBinEntries());
    }

    // Restore a selected entry back to the main list
    public void restoreEntry(ActionEvent event) {
        DiaryEntry selectedEntry = recycleBinListView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
        	String username = "currentUser"; // Replace with actual logic to get the current user's username

            // Restore the entry back to the main diary
            DiaryDataStore.restoreEntry(username, selectedEntry);
            // Reload the recycle bin list after restoring
            loadRecycleBinEntries();
        }
    }

    // Permanently delete a selected entry
    public void permanentDeleteEntry(ActionEvent event) {
        DiaryEntry selectedEntry = recycleBinListView.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            // Confirmation alert before permanent deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Permanent Delete");
            alert.setHeaderText("Are you sure you want to permanently delete this entry?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Delete the selected entry permanently
                    DiaryDataStore.permanentlyDeleteEntry(selectedEntry);
                    // Reload the recycle bin list after deletion
                    loadRecycleBinEntries();
                }
            });
        }
    }

    // Go back to the previous screen (Main Screen or View Entries)
    @FXML
    private void goBack(ActionEvent event) {
        try {
            // Assuming we're navigating back to the Main Screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}