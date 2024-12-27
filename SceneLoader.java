package application;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SceneLoader {
	
	public static Scene load(String fxmlFile) throws IOException {
        Parent root = FXMLLoader.load(SceneLoader.class.getResource(fxmlFile));
        return new Scene(root);
    }
	
	 public static Scene loadWithController(String fxmlFile, Object controller) throws IOException {
		 FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlFile));
	     loader.setController(controller); 
	     Parent root = loader.load();
	     return new Scene(root);
	 	}

}
