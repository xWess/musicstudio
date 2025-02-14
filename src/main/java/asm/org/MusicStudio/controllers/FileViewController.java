package asm.org.MusicStudio.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import asm.org.MusicStudio.dialogs.FileUploadDialog;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.CourseService;
import asm.org.MusicStudio.services.FileUploadService;
import asm.org.MusicStudio.services.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

public class FileViewController {
    @FXML private TableView<CourseFile> filesTable;
    @FXML private TableColumn<CourseFile, String> fileNameColumn;
    @FXML private TableColumn<CourseFile, String> courseColumn;
    @FXML private TableColumn<CourseFile, String> uploadDateColumn;
    @FXML private TableColumn<CourseFile, String> fileSizeColumn;
    @FXML private TableColumn<CourseFile, String> descriptionColumn;
    @FXML private TableColumn<CourseFile, String> teacherColumn;

    private final UserService userService;
    private final CourseService courseService;
    private final FileUploadService fileService;

    private User currentUser;

    public FileViewController() {
        this.userService = UserService.getInstance();
        this.courseService = CourseService.getInstance();
        this.fileService = FileUploadService.getInstance();
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
    private void handleFileUpload() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File to Upload");
            
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Music Files", "*.mp3", "*.wav")
            );
            
            File selectedFile = fileChooser.showOpenDialog(filesTable.getScene().getWindow());
            
            if (selectedFile != null) {
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                
                User currentUser = userService.getCurrentUser();
                if (currentUser == null || currentUser.getRole() != Role.TEACHER) {
                    showError("Error", "Only teachers can upload files");
                    return;
                }

                System.out.println("Current user: " + currentUser.getName() + " (ID: " + currentUser.getId() + ")");

                List<Course> courses = courseService.getCoursesByTeacher(currentUser.getId());
                System.out.println("Found " + courses.size() + " courses for teacher");
                
                if (courses.isEmpty()) {
                    showError("Error", "No courses found for the teacher");
                    return;
                }

                FileUploadDialog dialog = new FileUploadDialog(courses);
                Optional<FileUploadDialog.FileUploadResult> result = dialog.showAndWait();
                
                if (result.isPresent()) {
                    FileUploadDialog.FileUploadResult uploadResult = result.get();
                    System.out.println("Uploading file to course: " + uploadResult.course.getName());
                    
                    fileService.uploadFile(
                        selectedFile,
                        uploadResult.course.getId(),
                        currentUser.getId(),
                        uploadResult.description
                    );
                    
                    System.out.println("File upload completed, refreshing view");
                    loadFiles();
                    showSuccess("Success", "File uploaded successfully");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to upload file: " + e.getMessage());
        }
    }

    @FXML
    private void loadFiles() {
        try {
            if (currentUser == null) return;

            List<CourseFile> files = fileService.getFilesByTeacher(currentUser.getId());
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
            } catch (IOException | SQLException e) {
                showError("Error", "Failed to download file: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleFileDelete() {
        CourseFile selectedFile = filesTable.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            showError("Error", "Please select a file to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete File");
        confirm.setContentText("Are you sure you want to delete " + selectedFile.getFileName() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    fileService.deleteFile(selectedFile);
                    loadFiles();
                    showSuccess("Success", "File deleted successfully!");
                } catch (IOException | SQLException e) {
                    showError("Error", "Failed to delete file: " + e.getMessage());
                }
            }
        });
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

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadFiles();
    }
} 