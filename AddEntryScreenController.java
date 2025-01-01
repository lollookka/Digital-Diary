package application;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddEntryScreenController {
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	private TextField dateField;
	@FXML
	private TextField titleField;
	@FXML
	private TextArea contentArea;
	@FXML
	private Button uploadImageButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button cancelButton;
	@FXML
	private ImageView imageView;
	
	private String imagePath;
	
	private DiaryEntry currentEntry;
	
	public void setCurrentEntry(DiaryEntry entry) {
		this.currentEntry = entry;
		dateField.setText(entry.getDate());
		titleField.setText(entry.getTitle());
		contentArea.setText(entry.getContent());
		
		if (entry.getImagePath() != null && !entry.getImagePath().isEmpty()) {
			Image image = new Image(new File(entry.getImagePath()).toURI().toString());
			imageView.setImage(image);
		}
	}
	
	public void cancelEntry(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
	    Parent root = loader.load();
	    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    Scene scene = new Scene(root);
	    stage.setScene(scene);
	    stage.show();
	}
	
	private boolean validateInputs() {
		return !dateField.getText().isEmpty() &&
				!titleField.getText().isEmpty() &&
				!contentArea.getText().isEmpty();
	}
	
	public void saveEntry(ActionEvent event) {
		if(validateInputs()) {
			String date = dateField.getText();
			String title = titleField.getText();
			String content = contentArea.getText();
			String finalImagePath = this.imagePath != null? this.imagePath : "";
			
			DiaryEntry entry = new DiaryEntry(date, title, content, finalImagePath);
			
			if(currentEntry != null) {
				DiaryDataStore.updateEntry(currentEntry, entry);
			}else {
				DiaryDataStore.addEntry(entry);
			}
			
			dateField.clear();
			titleField.clear();
			contentArea.clear();
			imageView.setImage(null);
			imagePath = null;
			
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
	            Parent root = loader.load();
	            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	            Scene scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void uploadImage(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Image");
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
		);
		
		File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
		
		if (selectedFile != null) {
			imagePath = selectedFile.getAbsolutePath();
			Image image = new Image(selectedFile.toURI().toString());
			imageView.setImage(image);
		}
	}
	

}
