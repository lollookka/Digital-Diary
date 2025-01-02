package application;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;

public class ViewEntriesScreenController {
    
    @FXML
    private ListView<DiaryEntry> entriesListView;
    
    @FXML
    private TextField searchTextField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button backButton;
    
    private String currentUsername;
    
    // Set the username of the current user
    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }
    
    public void initialize() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            showAlert(AlertType.ERROR, "Username is not set. Please log in again.");
            return; // Exit if no username is set
        }
        
        List<DiaryEntry> entries = DiaryDataStore.getEntries(currentUsername);
        System.out.println("Entries loaded for " + currentUsername + ": " + entries.size());  // Check how many entries are loaded
        entriesListView.getItems().setAll(entries);
        
        // Set ListCell factory for displaying diary entries
        entriesListView.setCellFactory(param -> new ListCell<DiaryEntry>() {
            @Override
            protected void updateItem(DiaryEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (empty || entry == null) {
                    setText(null);
                } else {
                    setText(entry.getTitle());
                }
            }
        });
        
        // Enable or disable buttons based on selection
        entriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });
    }
    
    // Search for entries based on the keyword
    @FXML
    private void searchEntries(ActionEvent event) {
        String keyword = searchTextField.getText().trim();
        System.out.println("Search keyword: " + keyword);
        if (keyword.isEmpty()) {
            // Reset the ListView to show all entries
            entriesListView.getItems().setAll(DiaryDataStore.getEntries(currentUsername));
            return;
        }

        List<DiaryEntry> filteredEntries = filterEntries(keyword);
        if (filteredEntries.isEmpty()) {
            showAlert(AlertType.INFORMATION, "No entries found for the keyword: " + keyword);
        } else {
            entriesListView.getItems().setAll(filteredEntries);
        }
    }

    // Filter diary entries based on the keyword
    private List<DiaryEntry> filterEntries(String keyword) {
        List<DiaryEntry> allEntries = DiaryDataStore.getEntries(currentUsername);
        return allEntries.stream()
                         .filter(entry -> entry.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                          entry.getContent().toLowerCase().contains(keyword.toLowerCase()))
                         .collect(Collectors.toList());
    }

    // Display an alert with the given message
    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Edit the selected diary entry
    public void editEntry(ActionEvent event) throws IOException {
        DiaryEntry selectedEntry = entriesListView.getSelectionModel().getSelectedItem();
        
        if (selectedEntry != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddEntryScreen.fxml"));
            AnchorPane root = loader.load();
            AddEntryScreenController controller = loader.getController();
            controller.setCurrentEntry(selectedEntry);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
    
    // Delete the selected diary entry
    public void deleteEntry(ActionEvent event) throws IOException {
        DiaryEntry selectedEntry = entriesListView.getSelectionModel().getSelectedItem();

        if (selectedEntry != null) {
            // Use currentUsername instead of hardcoding
            String username = currentUsername;
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Entry");
            alert.setHeaderText("Are you sure you want to permanently delete this entry?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    DiaryDataStore.deleteEntry(username, selectedEntry);
                    entriesListView.getItems().remove(selectedEntry);
                }
            });
        }
    }
    
    // Navigate back to the main screen
    @FXML
    private void goBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
