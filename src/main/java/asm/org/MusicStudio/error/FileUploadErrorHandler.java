package asm.org.MusicStudio.error;

import asm.org.MusicStudio.logging.FileUploadLogger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class FileUploadErrorHandler {
    private FileUploadErrorHandler() {} // Prevent instantiation
    
    public static void handleError(String operation, Throwable e) {
        String errorMessage = String.format("Error during %s: %s", operation, e.getMessage());
        FileUploadLogger.error(errorMessage, e);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File Upload Error");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }
    
    public static void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    public static void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
} 