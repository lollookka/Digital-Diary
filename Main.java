package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Diary Management");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: Unable to load the main application screen.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
