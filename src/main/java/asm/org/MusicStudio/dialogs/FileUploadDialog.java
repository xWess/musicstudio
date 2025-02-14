package asm.org.MusicStudio.dialogs;

import java.util.List;

import asm.org.MusicStudio.entity.Course;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class FileUploadDialog extends Dialog<FileUploadDialog.FileUploadResult> {
    private final ComboBox<Course> courseCombo;
    private final TextArea descriptionArea;
    
    public FileUploadDialog(List<Course> courses) {
        setTitle("Upload File");
        setHeaderText("Select a course and add description");
        
        // Create layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));  // Réduit le padding droit
        
        courseCombo = new ComboBox<>(FXCollections.observableArrayList(courses));
        courseCombo.setPromptText("Select Course");
        
        // Personnaliser l'affichage pour ne montrer que le nom du cours
        courseCombo.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getName());
                }
            }
        });
        
        // Personnaliser l'affichage du cours sélectionné
        courseCombo.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getName());
                }
            }
        });
        
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter file description");
        descriptionArea.setPrefRowCount(3);
        
        grid.add(new Label("Course:"), 0, 0);
        grid.add(courseCombo, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        
        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/styles/dialog.css").toExternalForm()
        );
        
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new FileUploadResult(courseCombo.getValue(), descriptionArea.getText());
            }
            return null;
        });
    }
    
    public static class FileUploadResult {
        public final Course course;
        public final String description;
        
        public FileUploadResult(Course course, String description) {
            this.course = course;
            this.description = description;
        }
    }
} 