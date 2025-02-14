package asm.org.MusicStudio.controllers;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.services.CourseFileService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class FileManagementController {
    @FXML private TableView<CourseFile> fileTable;
    @FXML private Button uploadButton;
    @FXML private TextArea descriptionArea;
    @FXML private TableColumn<CourseFile, String> fileNameColumn;
    @FXML private TableColumn<CourseFile, String> uploadDateColumn;
    @FXML private TableColumn<CourseFile, String> fileTypeColumn;
    @FXML private TableColumn<CourseFile, String> descriptionColumn;
    @FXML private TableColumn<CourseFile, String> fileSizeColumn;
    @FXML private ComboBox<Integer> courseSelector;
    @FXML private Label statusLabel;
    @FXML private TextArea fileDescription;
    
    private CourseFileService fileService;
    private int currentTeacherId;
    private int currentCourseId;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        fileService = new CourseFileService();
        
        setupTable();
        setupCourseSelector();
        
        Platform.runLater(() -> {
            setupUploadButton();
            setupDeleteButton();
        });
    }

    public void setTeacherId(int teacherId) {
        this.currentTeacherId = teacherId;
        loadTeacherFiles();
    }

    private void setupTable() {
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        uploadDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUploadDate().format(DATE_FORMATTER)));
        fileTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        fileSizeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFormattedSize()));
            
        // Enable multiple selection
        fileTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Add selection listener
        fileTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                updateStatus("Selected: " + newSelection.getFileName(), false);
            }
        });
    }

    private void setupUploadButton() {
        if (uploadButton != null) {
            uploadButton.setOnAction(event -> handleUpload());
        }
    }

    @FXML
    private void handleUpload() {
        if (courseSelector.getValue() == null) {
            updateStatus("Please select a course first", true);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Supported Files", "*.pdf", "*.doc", "*.docx", "*.txt"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String description = fileDescription.getText();
                CourseFile uploadedFile = fileService.uploadFile(
                    selectedFile,
                    courseSelector.getValue(),
                    currentTeacherId,
                    description
                );
                refreshFiles();
                updateStatus("File '" + uploadedFile.getFileName() + "' uploaded successfully", false);
                fileDescription.clear();
            } catch (Exception e) {
                updateStatus("Upload failed: " + e.getMessage(), true);
                showError("Upload Error", e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete() {
        CourseFile selectedFile = fileTable.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            updateStatus("Please select a file to delete", true);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete File");
        confirmation.setContentText("Are you sure you want to delete '" + selectedFile.getFileName() + "'?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                fileService.deleteFile(selectedFile);
                refreshFiles();
                updateStatus("File deleted successfully", false);
            } catch (Exception e) {
                updateStatus("Failed to delete file: " + e.getMessage(), true);
            }
        }
    }

    private void setupDeleteButton() {
        // No need for implementation since the delete button's action
        // is already set in FXML with onAction="#handleDelete"
    }

    private void loadTeacherFiles() {
        if (currentTeacherId > 0) {
            // Load course IDs for the teacher
            List<Integer> courseIds = fileService.getTeacherCourseIds(currentTeacherId);
            Platform.runLater(() -> {
                courseSelector.getItems().clear();
                courseSelector.getItems().addAll(courseIds);
                
                if (!courseIds.isEmpty()) {
                    courseSelector.setValue(courseIds.get(0));
                    currentCourseId = courseIds.get(0);
                    refreshFiles();
                } else {
                    updateStatus("No courses found for this teacher", true);
                }
            });
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void refreshFiles() {
        if (currentCourseId > 0) {
            fileTable.getItems().clear();
            fileTable.getItems().addAll(fileService.getFilesByCourse(currentCourseId));
        }
    }

    private void updateStatus(String message, boolean isError) {
        Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        });
    }

    private void setupCourseSelector() {
        List<Integer> courseIds = fileService.getTeacherCourseIds(currentTeacherId);
        courseSelector.getItems().addAll(courseIds);
        courseSelector.setOnAction(event -> {
            currentCourseId = courseSelector.getValue();
            refreshFiles();
        });
    }

    @FXML
    private void handleDownload() {
        CourseFile selectedFile = fileTable.getSelectionModel().getSelectedItem();
        if (selectedFile == null) {
            updateStatus("Please select a file from the table first", true);
            return;
        }

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Download Location");
        File targetDir = dirChooser.showDialog(fileTable.getScene().getWindow());
        
        if (targetDir != null) {
            try {
                fileService.downloadFile(selectedFile, targetDir.getAbsolutePath());
                updateStatus("File '" + selectedFile.getFileName() + "' downloaded successfully", false);
            } catch (Exception e) {
                updateStatus("Download failed: " + e.getMessage(), true);
            }
        }
    }
} 