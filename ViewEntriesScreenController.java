package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ViewEntriesScreenController {
	
	@FXML
    private ListView<DiaryEntry> entryListView;
	
	private List<DiaryEntry> entries;

    @FXML
    private void initialize() {
        entries = ReadDiaryEntry.readDiaryEntries(); 
        if (entries != null) {
            entryListView.getItems().setAll(entries);
            
            entryListView.setCellFactory(listView -> new ListCell<DiaryEntry>() {
                @Override
                protected void updateItem(DiaryEntry entry, boolean empty) {
                    super.updateItem(entry, empty);
                    if (entry == null || empty) {
                        setText(null);
                    } else {
                        setText(entry.getTitle() + " (" + entry.getDate() + ")");
                    }
                }
            });
        }
    }

    @FXML
    private void editEntry() {
        DiaryEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();
        if (selectedEntry == null) {
            showAlert("Error", "No entry selected", "Please select an entry to edit.");
            return;
        }

        try {
        	FXMLLoader loader = new FXMLLoader(getClass().getResource("EditEntryScreen.fxml"));
            Parent root = loader.load();

            EditEntryScreenController controller = loader.getController();
            controller.setEntry(selectedEntry, entries);  
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void deleteEntry() {
        DiaryEntry selectedEntry = entryListView.getSelectionModel().getSelectedItem();  
        System.out.println("Selected entry: " + selectedEntry);
        
        if (selectedEntry == null) {
            showAlert("Error", "No entry selected", "Please select an entry to delete.");
            return;
        }
        
        if (showConfirmationDialog("Delete Entry", "Are you sure you want to delete this entry?")) {
            entries.remove(selectedEntry);
            SaveDiaryEntry.saveAllEntries(entries); 
            entryListView.getItems().setAll(entries); 
            System.out.println("Entry deleted: " + selectedEntry.getTitle());
            showConfirmation("Entry deleted successfully.");
        }
    }
    
    @FXML
    private void backEntry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) entryListView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the Main screen.");
        }
    }
        
    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }    

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
