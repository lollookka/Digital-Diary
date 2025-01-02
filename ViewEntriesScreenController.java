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
	
	public void setCurrentUsername(String username) {
	    this.currentUsername = username;
	}
	
	public void initialize() {
		
		List<DiaryEntry> entries = DiaryDataStore.getEntries(currentUsername);
	    
	    entriesListView.getItems().setAll(entries);
	    
	    
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
	    
	    
	    entriesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	        editButton.setDisable(newSelection == null);
	        deleteButton.setDisable(newSelection == null);
	    });
	}
	
	@FXML
	private void searchEntries(ActionEvent event) {
	    String keyword = searchTextField.getText().trim();
	    if (keyword.isEmpty()) {
	        // Optionally, reset the ListView to show all entries
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


    // Filter entries based on the keyword
    private List<DiaryEntry> filterEntries(String keyword) {
        List<DiaryEntry> allEntries = DiaryDataStore.getEntries(currentUsername);
        return allEntries.stream()
                         .filter(entry -> entry.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                          entry.getContent().toLowerCase().contains(keyword.toLowerCase()))
                         .collect(Collectors.toList());
    }
    
    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
	
	public void editEntry(ActionEvent event) throws IOException {
		DiaryEntry selectedEntry = entriesListView.getSelectionModel().getSelectedItem();
		
		if (selectedEntry != null) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AddEntryScreen.fxml"));
			AnchorPane root = loader.load();
			AddEntryScreenController controller = loader.getController();
			controller.setCurrentEntry(selectedEntry);
			
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
	}
	
	public void deleteEntry(ActionEvent event) throws IOException {
		DiaryEntry selectedEntry = entriesListView.getSelectionModel().getSelectedItem();

        if (selectedEntry != null) {
        	
        	String username = "currentUser";
            
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