package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.services.CourseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.sql.SQLException;
import java.time.LocalDate;

public class EnrollmentDialogController {
    @FXML
    private ComboBox<Course> courseComboBox;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private Spinner<Integer> durationSpinner;
    
    @FXML
    private Label totalCostLabel;
    
    private CourseService courseService;
    
    @FXML
    public void initialize() {
        courseService = new CourseService();
        
        // Initialize the spinner
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        durationSpinner.setValueFactory(valueFactory);
        
        // Set default date to today
        startDatePicker.setValue(LocalDate.now());
        
        // Load available courses
        loadCourses();
        
        // Add listeners for updating total cost
        courseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalCost());
        durationSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalCost());
    }
    
    private void loadCourses() {
        try {
            courseComboBox.setItems(FXCollections.observableArrayList(
                courseService.getAvailableCourses()
            ));
        } catch (SQLException e) {
            showError("Error", "Failed to load courses: " + e.getMessage());
        }
    }
    
    private void updateTotalCost() {
        Course selectedCourse = courseComboBox.getValue();
        Integer duration = durationSpinner.getValue();
        
        if (selectedCourse != null && duration != null) {
            double totalCost = selectedCourse.getMonthlyFee() * duration;
            totalCostLabel.setText(String.format("Total Cost: $%.2f", totalCost));
        }
    }
    
    public void processEnrollment() {
        Course selectedCourse = courseComboBox.getValue();
        LocalDate startDate = startDatePicker.getValue();
        int duration = durationSpinner.getValue();
        
        if (selectedCourse == null) {
            showError("Error", "Please select a course");
            return;
        }
        
        try {
            courseService.enrollStudent(selectedCourse, startDate, duration);
            showSuccess("Success", "Successfully enrolled in the course!");
        } catch (SQLException e) {
            showError("Error", "Failed to process enrollment: " + e.getMessage());
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