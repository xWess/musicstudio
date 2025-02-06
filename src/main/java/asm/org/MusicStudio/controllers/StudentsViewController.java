package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.CourseService;
import asm.org.MusicStudio.services.EnrollmentService;
import asm.org.MusicStudio.services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class StudentsViewController {
    @FXML private TableView<Enrollment> studentsTable;
    @FXML private TableColumn<Enrollment, String> nameColumn;
    @FXML private TableColumn<Enrollment, String> emailColumn;
    @FXML private TableColumn<Enrollment, String> courseColumn;
    @FXML private TableColumn<Enrollment, String> progressColumn;
    @FXML private TableColumn<Enrollment, String> lastAttendanceColumn;
    @FXML private ComboBox<Course> courseFilter;
    @FXML private TextField searchField;

    private UserService userService;
    private EnrollmentService enrollmentService;
    private CourseService courseService;
    private int currentTeacherId;

    @FXML
    public void initialize() {
        System.out.println("Initializing StudentsViewController...");
        enrollmentService = new EnrollmentService();
        courseService = new CourseService();
        System.out.println("Services initialized");
        setupTableColumns();
        System.out.println("Table columns setup completed");
        setupFilters();
        System.out.println("Filters setup completed");
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> {
            User student = cellData.getValue().getStudent();
            return new SimpleStringProperty(student != null ? student.getName() : "");
        });
        
        emailColumn.setCellValueFactory(cellData -> {
            User student = cellData.getValue().getStudent();
            return new SimpleStringProperty(student != null ? student.getEmail() : "");
        });
        
        courseColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue().getCourse();
            return new SimpleStringProperty(course != null ? course.getName() : "");
        });
        
        progressColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty("Active"));  // Default status
            
        lastAttendanceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(""));  // Empty since we don't have start date
    }

    private void setupFilters() {
        courseFilter.setOnAction(e -> filterStudents());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterStudents());
        loadCourseFilter();
    }

    private void filterStudents() {
        String searchText = searchField.getText().toLowerCase();
        Course selectedCourse = courseFilter.getValue();
        
        ObservableList<Enrollment> filteredList = FXCollections.observableArrayList(
            enrollmentService.getEnrollmentsByTeacher(currentTeacherId).stream()
                .filter(enrollment -> {
                    boolean matchesSearch = searchText.isEmpty() || 
                        enrollment.getStudent().getName().toLowerCase().contains(searchText) ||
                        enrollment.getStudent().getEmail().toLowerCase().contains(searchText);
                    boolean matchesCourse = selectedCourse == null || 
                        enrollment.getCourse().getId() == selectedCourse.getId();
                    return matchesSearch && matchesCourse;
                })
                .toList()
        );
        
        studentsTable.setItems(filteredList);
    }

    private void loadCourseFilter() {
        try {
            courseFilter.setItems(FXCollections.observableArrayList(
                courseService.getCoursesByTeacher(currentTeacherId)
            ));
        } catch (Exception e) {
            showError("Error", "Failed to load courses: " + e.getMessage());
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setCurrentTeacherId(int teacherId) {
        System.out.println("Setting teacher ID to: " + teacherId);
        this.currentTeacherId = teacherId;
        loadStudents();
    }

    private void loadStudents() {
        try {
            System.out.println("Loading students for teacher ID: " + currentTeacherId);
            
            ObservableList<Enrollment> enrollments = FXCollections.observableArrayList(
                enrollmentService.getEnrollmentsByTeacher(currentTeacherId)
            );
            
            System.out.println("Found " + enrollments.size() + " enrollments");
            enrollments.forEach(e -> 
                System.out.println("Student: " + e.getStudent().getName() + 
                                 ", Course: " + e.getCourse().getName())  // Removed status
            );
            
            studentsTable.setItems(enrollments);
            System.out.println("Students table updated successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading students: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to load students: " + e.getMessage());
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