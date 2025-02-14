package asm.org.MusicStudio.controllers;

import java.io.File;
import java.time.format.DateTimeFormatter;

import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.FileUploadService;
import asm.org.MusicStudio.services.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

public class StudentFileViewController {
    @FXML private TableView<CourseFile> filesTable;
    @FXML private TableColumn<CourseFile, String> fileNameColumn;
    @FXML private TableColumn<CourseFile, String> courseColumn;
    @FXML private TableColumn<CourseFile, String> uploadDateColumn;
    @FXML private TableColumn<CourseFile, String> fileSizeColumn;
    @FXML private TableColumn<CourseFile, String> descriptionColumn;
    @FXML private TableColumn<CourseFile, String> teacherColumn;

    private final FileUploadService fileService;
    private final UserService userService;

    public StudentFileViewController() {
        this.fileService = FileUploadService.getInstance();
        this.userService = UserService.getInstance();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadFiles();
    }

    private void setupTableColumns() {
        fileNameColumn.setCellValueFactory(cellData -> 
            cellData.getValue().fileNameProperty());
        courseColumn.setCellValueFactory(cellData -> 
            cellData.getValue().courseNameProperty());
        teacherColumn.setCellValueFactory(cellData -> 
            cellData.getValue().teacherNameProperty());
        descriptionColumn.setCellValueFactory(cellData -> 
            cellData.getValue().descriptionProperty());
        
        uploadDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String date = cellData.getValue().getUploadDate().format(formatter);
            return javafx.beans.binding.Bindings.createStringBinding(() -> date);
        });

        fileSizeColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getFormattedSize()
            )
        );
    }

    @FXML
    private void loadFiles() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) return;

            var files = fileService.getFilesByStudentEnrollment(currentUser.getId());
            filesTable.setItems(FXCollections.observableArrayList(files));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load files: " + e.getMessage());
        }
    }

    @FXML
    private void handleFileDownload() {
        CourseFile selectedFile = filesTable.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            showError("Error", "Please select a file to download");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(selectedFile.getFileName());
        File targetFile = fileChooser.showSaveDialog(filesTable.getScene().getWindow());
        
        if (targetFile != null) {
            try {
                fileService.downloadFile(selectedFile, targetFile);
                showSuccess("Success", "File downloaded successfully!");
            } catch (Exception e) {
                showError("Error", "Failed to download file: " + e.getMessage());
            }
        }
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 