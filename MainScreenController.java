package application;

import javafx.event.ActionEvent;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainScreenController {
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void switchToAddEntry(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("AddEntryScreen.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
	public void switchToViewEntries(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("ViewEntriesScreen.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
}
