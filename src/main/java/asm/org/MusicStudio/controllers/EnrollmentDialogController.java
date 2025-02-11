package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.services.CourseService;
import asm.org.MusicStudio.services.EnrollmentServiceImpl;
import asm.org.MusicStudio.services.PaymentService;
import asm.org.MusicStudio.services.StudentServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EnrollmentDialogController {
    @FXML
    private ComboBox<Course> courseComboBox;
    
    @FXML
    private DatePicker startDatePicker;
    
    @FXML
    private Spinner<Integer> durationSpinner;
    
    @FXML
    private Label totalCostLabel;
    
    private PaymentService paymentService;
    private EnrollmentServiceImpl enrollmentService;
    private StudentServiceImpl studentService;
    private Student currentStudent;
    private Dialog<?> dialog;


    @FXML
    public void initialize() {
        paymentService = new PaymentService();
        enrollmentService = new EnrollmentServiceImpl();
        studentService = new StudentServiceImpl();
        
        setupControls();
        setupListeners();
        
        // Initialize the total cost label
        totalCostLabel.setText("Total Cost: $0.00");
    }

    public void setCurrentStudent(Student student) {
        if (student == null) {
            showError("Error", "Student cannot be null");
            return;
        }
        this.currentStudent = student;
        System.out.println("Setting current student: " + student.getId()); // Debug log
        
        // Load courses after setting the student
        loadAvailableCourses();
        
        // Update the total cost display
        updateTotalCost();
    }

    @FXML
    public void handleEnroll() {
        try {
            if (currentStudent == null) {
                showError("Error", "No student selected");
                return;
            }
            
            Course selectedCourse = courseComboBox.getValue();
            if (selectedCourse == null) {
                showError("Error", "Please select a course");
                return;
            }

            // First check if student can enroll
            try {
                if (!enrollmentService.canEnroll(currentStudent, selectedCourse)) {
                    showError("Enrollment Error", 
                        String.format("You are already enrolled in %s. Please choose a different course.", 
                        selectedCourse.getName()));
                    return;
                }
            } catch (SQLException e) {
                showError("Error", "Failed to check enrollment eligibility: " + e.getMessage());
                return;
            }

            // Only process payment if enrollment is possible
            Payment payment = paymentService.processEnrollmentPayment(
                currentStudent,
                selectedCourse, 
                durationSpinner.getValue()
            );
            
            // Create enrollment
            enrollmentService.createEnrollment(
                currentStudent, 
                selectedCourse, 
                startDatePicker.getValue(), 
                payment
            );

            showSuccess("Success", "Successfully enrolled in course!");
            dialog.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to complete enrollment: " + e.getMessage());
        }
    }

    private void loadAvailableCourses() {
        try {
            List<Course> courses = studentService.getAvailableCourses();
            courseComboBox.setItems(FXCollections.observableArrayList(courses));
        } catch (SQLException e) {
            showError("Error", "Failed to load courses: " + e.getMessage());
        }
    }


    private void setupControls() {
        // Initialize the spinner
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        durationSpinner.setValueFactory(valueFactory);
        
        // Set default date to today
        startDatePicker.setValue(LocalDate.now());
    }

    private void setupListeners() {
        // Add listeners for updating total cost
        courseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalCost());
        durationSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalCost());
    }
    
    private void updateTotalCost() {
        Course selectedCourse = courseComboBox.getValue();
        Integer duration = durationSpinner.getValue();
        
        if (selectedCourse != null && duration != null) {
            double totalCost = selectedCourse.getMonthlyFee() * duration;
            totalCostLabel.setText(String.format("Total Cost: $%.2f", totalCost));
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
        alert.setHeaderText("Enrollment Successful!");
        
        Course selectedCourse = courseComboBox.getValue();
        int duration = durationSpinner.getValue();
        double totalCost = selectedCourse.getMonthlyFee() * duration;
        
        String message = String.format("""
            Enrollment Details:
            
            Course: %s
            Duration: %d months
            Start Date: %s
            Total Cost: $%.2f
            
            Thank you for enrolling!""",
            selectedCourse.getName(),
            duration,
            startDatePicker.getValue(),
            totalCost);
            
        alert.setContentText(message);
        
        // Make the dialog wider
        alert.getDialogPane().setMinWidth(400);
        
        // Add custom styling if needed
        alert.getDialogPane().getStyleClass().add("success-dialog");
        
        alert.showAndWait();
    }

    public void setDialog(Dialog<?> dialog) {
        this.dialog = dialog;
    }

    private void showDuplicateEmailError(String email) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText("Email Already Registered");
        
        String message = String.format("""
            The email address '%s' is already registered in our system.
            
            Please either:
            • Use a different email address
            • Login with your existing account
            • Contact support if you need assistance
            
            Note: Each user must have a unique email address.""",
            email);
            
        alert.setContentText(message);
        
        // Make the dialog wider
        alert.getDialogPane().setMinWidth(400);
        
        // Add custom styling
        alert.getDialogPane().getStyleClass().add("duplicate-email-error");
        
        alert.showAndWait();
    }
} 