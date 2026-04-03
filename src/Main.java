import utils.FileHandler;
import ui.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize local data files if they don't exist
        FileHandler.initializeFiles();
        
        primaryStage.setTitle("APU Automotive Service Centre");
        // Optional: add a generic JavaFX icon if available, skipped since we don't have media path.
        
        LoginView loginView = new LoginView(primaryStage);
        Scene scene = loginView.createScene();
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
