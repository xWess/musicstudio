package asm.org.MusicStudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.util.WindowManager;

public class MusicStudioApplication extends Application {
    
    @Override
    public void init() throws Exception {
        super.init();
        // Initialize database connection
        try {
            DatabaseConnection.getInstance().getConnection();
            System.out.println("Database connection established successfully");
        } catch (Exception e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            primaryStage.setTitle("Music Studio Login");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}