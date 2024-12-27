package application;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddEntryScreencontroller {
	
	@FXML
    private TextField dateField;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea contentArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;
    @FXML 
    private Button uploadImageButton;
    @FXML
    private Label errorLabel;
    @FXML
    private ImageView imageView; 
    
    private String imagePath;

    @FXML
    private void initialize() {
        saveButton.setOnAction(event -> saveEntry());
        backButton.setOnAction(event -> goBackToMain());
        uploadImageButton.setOnAction(event -> uploadImage());
    }
    
    @FXML
    private void uploadImage() {
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();  
            System.out.println("Image selected: " + imagePath);  
            
            Image image = new Image("file:" + imagePath);
            imageView.setImage(image);
        } else {
            imagePath = null;  
            System.out.println("No image selected.");
            imageView.setImage(null);
        }
    }

    @FXML
    private void saveEntry() {
    	String date = dateField.getText();
        String title = titleField.getText();
        String content = contentArea.getText();

        if (date.isEmpty() || title.isEmpty() || content.isEmpty()) {
            System.out.println("All fields must be filled.");
            return;
        }
        
        if (!isValidDate(date)) {
            showError("Invalid date format. Use DD-MM-YYYY.");
            return;
        }

       
        System.out.println("Saving entry with date: " + date +
                ", title: " + title +
                ", content: " + content +
                ", imagePath: " + imagePath);

        DiaryEntry entry = new DiaryEntry(date, title, content, imagePath);
        SaveDiaryEntry.saveEntry(entry); 
         
        clearFields();
        showError("Entry saved successfully!");
    }

    private void clearFields() {
    	dateField.clear();
        titleField.clear();
        contentArea.clear();  
        imagePath = null;
        errorLabel.setVisible(false);
        imageView.setImage(null);
	}

	private void showError(String message) {
		errorLabel.setText(message);
	    errorLabel.setVisible(true);
	}

	private boolean isValidDate(String date) {
		return date.matches("\\d{2}-\\d{2}-\\d{4}");
	}

	@FXML
	private void goBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow(); 
            stage.setScene(new Scene(loader.load())); 
            stage.show(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
