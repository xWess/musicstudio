package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.services.EnrollmentService;
import asm.org.MusicStudio.services.EnrollmentServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.beans.property.SimpleStringProperty;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;

import asm.org.MusicStudio.controllers.EnrollmentDialogController;
import asm.org.MusicStudio.services.StudentService;
import asm.org.MusicStudio.services.StudentServiceImpl;
import asm.org.MusicStudio.services.PaymentService;
import javafx.scene.layout.VBox;
import javafx.scene.control.DialogPane;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;


public class EnrollmentViewController {
    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> courseNameColumn;
    @FXML private TableColumn<Enrollment, String> instructorColumn;
    @FXML private TableColumn<Enrollment, String> scheduleColumn;
    @FXML private TableColumn<Enrollment, String> enrollmentStatusColumn;
    @FXML private ComboBox<String> semesterFilter;

    private EnrollmentService enrollmentService;
    private StudentService studentService;
    private PaymentService paymentService;
    private Student currentStudent;
    private User currentUser;

    @FXML
    public void initialize() {
        System.out.println("Initializing EnrollmentViewController");
        try {
            // Initialize services
            enrollmentService = new EnrollmentServiceImpl();
            studentService = new StudentServiceImpl();
            paymentService = new PaymentService();
            
            // Setup table columns
            setupTableColumns();
            
        } catch (Exception e) {
            showError("Error", "Failed to initialize enrollment view: " + e.getMessage());
        }
    }

    public void setCurrentUser(User user) {
        System.out.println("Setting current user in EnrollmentViewController: " + 
            (user != null ? user.getName() : "null"));
        
        if (user == null) {
            System.out.println("Warning: Attempting to set null user");
            return;
        }
        
        this.currentUser = user;
        
        if (user.getRole() != Role.STUDENT) {
            System.out.println("Warning: User is not a student. Role: " + user.getRole());
            return;
        }
        
        try {
            // Get StudentService instance
            StudentService studentService = new StudentServiceImpl();
            
            // Convert User to Student using UserDAO instead of StudentDAO
            if (user instanceof Student) {
                this.currentStudent = (Student) user;
            } else {
                // Create a new Student instance with the user's data
                this.currentStudent = new Student(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
                );
            }
            
            System.out.println("Successfully set current student: " + currentStudent.getId());
            
            // Now load enrollments
            loadEnrollments();
            
        } catch (Exception e) {
            System.out.println("Error setting current student: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        if (student != null) {
            loadEnrollments();
        }
    }

    @FXML
    public void showEnrollmentDialog() {
        if (currentUser == null) {
            System.out.println("Error: Cannot show enrollment dialog - currentUser is null");
            showError("Error", "No user logged in");
            return;
        }
        
        try {
            if (currentStudent == null) {
                showError("Error", "No student selected");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EnrollmentDialog.fxml"));
            VBox dialogContent = loader.load();
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Course Enrollment");
            dialog.setHeaderText("Enroll in a New Course");
            
            DialogPane dialogPane = new DialogPane();
            dialogPane.setContent(dialogContent);
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.setDialogPane(dialogPane);
            
            EnrollmentDialogController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);
            controller.setDialog(dialog);
            
            // Remove the setOnAction and use resultConverter instead
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    controller.handleEnroll();
                    loadEnrollments();
                }
                return dialogButton;
            });
            
            dialog.showAndWait();
            loadEnrollments();
        } catch (IOException e) {
            showError("Error", "Failed to show enrollment dialog: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        courseNameColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getCourse().getName()));
        instructorColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getCourse().getInstructor()));
        scheduleColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStartDate().toString()));
        enrollmentStatusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus()));
    }

    private void loadEnrollments() {
        try {
            System.out.println("Loading enrollments for student: " + currentStudent.getId());
            List<Enrollment> enrollments = enrollmentService.findCurrentEnrollmentsByStudent(currentStudent.getId());
            enrollmentTable.setItems(FXCollections.observableArrayList(enrollments));
            System.out.println("Loaded " + enrollments.size() + " enrollments");

        } catch (SQLException e) {
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