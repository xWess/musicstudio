package asm.org.MusicStudio.controllers;

import java.sql.SQLException;
import java.util.List;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.CourseService;
import asm.org.MusicStudio.services.EnrollmentService;
import asm.org.MusicStudio.services.EnrollmentServiceImpl;
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
        enrollmentService = EnrollmentServiceImpl.getInstance();
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
        try {
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
        } catch (SQLException e) {
            System.err.println("Error filtering students: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to filter students: " + e.getMessage());
        }
    }

    private void loadCourseFilter() {
        try {
            List<Course> courses = courseService.getCoursesByTeacher(currentTeacherId);
            courseFilter.setItems(FXCollections.observableArrayList(courses));
        } catch (SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to load courses: " + e.getMessage());
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setCurrentTeacherId(int teacherId) {
        System.out.println("Setting teacher ID to: " + teacherId);
        this.currentTeacherId = teacherId;
        loadCourseFilter();  // This is where it starts failing
    }

    @FXML
    private void loadEnrollments() {
        try {
            System.out.println("Loading enrollments for teacher: " + currentTeacherId);
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByTeacher(currentTeacherId);
            studentsTable.setItems(FXCollections.observableArrayList(enrollments));
            System.out.println("Loaded " + enrollments.size() + " enrollments");
        } catch (SQLException e) {
            System.err.println("Error loading enrollments: " + e.getMessage());
            e.printStackTrace();
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