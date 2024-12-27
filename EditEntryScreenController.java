package application;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EditEntryScreenController {
	
	
	@FXML
    private TextField editDate;
    @FXML
    private TextField editTitle;
    @FXML
    private TextArea editContent;
    @FXML
    private Button cancelEdit;
    @FXML
    private Button saveEdit;
    @FXML
    private ImageView imageView;
    
    private DiaryEntry entry; 
    private List<DiaryEntry> entries;

    public void setEntry(DiaryEntry entry, List<DiaryEntry> entries) {
    	if (entry == null) {
            System.out.println("Received null entry to edit.");
            return;
        }

    	this.entry = entry;
        this.entries = entries;
        System.out.println("Editing Entry: " + entry.getTitle() + ", Date: " + entry.getDate() + ", Content: " + entry.getContent());
        editDate.setText(entry.getDate());
        editTitle.setText(entry.getTitle()); 
        editContent.setText(entry.getContent() != null && !entry.getContent().isEmpty() ? entry.getContent() : "No content provided");
    
        if (entry.getImagePath() != null) {
            Image image = new Image("file:" + entry.getImagePath());
            imageView.setImage(image); 
        }
    }

    @FXML
    private void saveEntry() {
    	System.out.println("Saving entry with date: " + editDate.getText() +
                ", title: " + editTitle.getText() +
                ", content: " + editContent.getText());
    	
    	if (editDate.getText().isEmpty() || editTitle.getText().isEmpty() || editContent.getText().isEmpty()) {
            showError("All fields must be filled before saving.");
            return;
        }

        if (!isValidDate(editDate.getText())) {
            showError("Invalid date format. Use DD-MM-YYYY.");
            return;
        }

        System.out.println("Before Update: " + entry);
        entry.setDate(editDate.getText());
        entry.setTitle(editTitle.getText());
        entry.setContent(editContent.getText());
        
        String newImagePath = ""; 
        if (newImagePath != null && !newImagePath.isEmpty()) {
            entry.setImagePath(newImagePath); 
        }
        
        System.out.println("After Update: " + entry);
        
        SaveDiaryEntry.saveAllEntries(entries);
        System.out.println("Saved Entries: ");
        entries.forEach(e -> System.out.println(e));
        
        showConfirmation("Entry saved successfully!");
        goBack();
    }
    
    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (file != null) {
            String imagePath = file.getAbsolutePath();
            
            entry.setImagePath(imagePath);
            Image image = new Image("file:" + imagePath);
            imageView.setImage(image);
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    @FXML
    private void cancelEdit() {
    	if (!editDate.getText().equals(entry.getDate()) ||
    		!editTitle.getText().equals(entry.getTitle()) ||
            !editContent.getText().equals(entry.getContent())) {

            if (!showConfirmationDialog("Unsaved Changes", "You have unsaved changes. Discard changes?")) {
            	return;
            }
        }
        goBack();
    }
    
    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewEntriesScreen.fxml"));
            Stage stage = (Stage) editDate.getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load the View Entries screen.");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showConfirmation(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Confirmation", message);
    }

}
