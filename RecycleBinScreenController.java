package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class RecycleBinScreenController {

	@FXML
	private ListView<DiaryEntry> recycleBinListView;
	@FXML
	private Button deleteButton;
	@FXML
	private Button backButton;

	@FXML
	private void initialize() {
		if (RecycleBin.getTrashBin().isEmpty()) {
            showAlert("Recycle Bin", "No Entries", "The recycle bin is empty.");
        } else {
            recycleBinListView.getItems().setAll(RecycleBin.getTrashBin());
        }

        recycleBinListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deleteButton.setDisable(newValue == null);
        });
	}
	
	private boolean showConfirmationAlert(String title, String content) {
	    Alert alert = new Alert(AlertType.CONFIRMATION);
	    alert.setTitle(title);
	    alert.setContentText(content);
	    return alert.showAndWait().get() == ButtonType.OK;
	}

	@FXML
	private void deletePermanently() {
		DiaryEntry selectedEntry = recycleBinListView.getSelectionModel().getSelectedItem();

	    if (selectedEntry == null) {
	    	showAlert("Error", "No entry selected", "Please select an entry to delete permanently.");
	        return;
	    }

	    boolean confirmed = showConfirmationAlert("Delete Entry", "Are you sure you want to permanently delete this entry?");
	    if (confirmed) {
	        RecycleBin.removeEntry(selectedEntry);
	        SaveDiaryEntry.saveAllEntries(RecycleBin.getTrashBin());
	        recycleBinListView.getItems().setAll(RecycleBin.getTrashBin());
	        showAlert("Success", "Entry deleted", "The selected entry has been permanently deleted.");
	    }
	}

	 @FXML
	 private void goBack() {
		 try {
			 FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
	         Stage stage = (Stage) recycleBinListView.getScene().getWindow();
	         stage.setScene(new Scene(loader.load()));
	         stage.show();
	      } catch (IOException e) {
	          e.printStackTrace();
	      }
	 }

	 private void showAlert(String title, String header, String content) {
	     Alert alert = new Alert(AlertType.INFORMATION);
	     alert.setTitle(title);
	     alert.setHeaderText(header);
	     alert.setContentText(content);
	     alert.showAndWait();
	 }

}
