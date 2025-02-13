package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.services.EnrollmentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import asm.org.MusicStudio.dialogs.EnrollmentDialog;
import javafx.beans.property.SimpleStringProperty;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.User;

public class EnrollmentViewController {
    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> courseNameColumn;
    @FXML private TableColumn<Enrollment, String> instructorColumn;
    @FXML private TableColumn<Enrollment, String> scheduleColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentStatusColumn;
    @FXML private ComboBox<String> semesterFilter;

    private EnrollmentService enrollmentService;
    private ObservableList<Enrollment> enrollmentData;

    @FXML
    public void initialize() {
        // Initialize services
        enrollmentService = new EnrollmentService();
        enrollmentData = FXCollections.observableArrayList();

        // Setup semester filter
        semesterFilter.setItems(FXCollections.observableArrayList(
            "All Semesters", "Spring 2024", "Summer 2024", "Fall 2024"
        ));
        semesterFilter.setValue("All Semesters");
        semesterFilter.setOnAction(e -> loadEnrollments());

        // Setup table columns
        setupTableColumns();

        // Load initial data
        loadEnrollments();
    }

    @FXML
    public void showEnrollmentDialog() {
        try {
            // Show enrollment dialog
            Dialog<Enrollment> dialog = new EnrollmentDialog();
            dialog.showAndWait().ifPresent(enrollment -> {
                enrollmentService.enrollInCourse(enrollment);
                loadEnrollments();
            });
        } catch (Exception e) {
            showError("Error", "Failed to show enrollment dialog: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        courseNameColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue().getCourse();
            return new SimpleStringProperty(course != null ? course.getName() : "");
        });
        
        instructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue().getCourse();
            User instructor = course != null ? course.getInstructor() : null;
            return new SimpleStringProperty(instructor != null ? instructor.getName() : "");
        });
        
        scheduleColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue().getCourse();
            return new SimpleStringProperty(course != null ? course.getSchedule() : "");
        });
        
        enrollmentStatusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
    }

    private void loadEnrollments() {
        try {
            String semester = semesterFilter.getValue();
            enrollmentData.clear();
            enrollmentData.addAll(enrollmentService.getEnrollments(semester));
            enrollmentTable.setItems(enrollmentData);
        } catch (Exception e) {
            showError("Error", "Failed to load enrollments: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 