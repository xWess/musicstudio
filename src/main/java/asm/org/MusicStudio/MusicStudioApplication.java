package asm.org.MusicStudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import asm.org.MusicStudio.controllers.MainController;
import asm.org.MusicStudio.services.UserService;
import asm.org.MusicStudio.services.UserServiceImpl;

public class MusicStudioApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();
        
        // Get the controller and set the service
        MainController controller = loader.getController();
        UserService userService = new UserServiceImpl();
        controller.setUserService(userService);
        
        // Create the scene
        Scene scene = new Scene(root, 1024, 768);
        
        // Configure the stage
        primaryStage.setTitle("Music Studio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}