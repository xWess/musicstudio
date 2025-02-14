package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.CourseService;
import asm.org.MusicStudio.services.UserService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.GridPane;

public class TeacherStudentViewController {
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> enrollmentDateColumn;
    @FXML private TableColumn<Student, String> statusColumn;
    
    private final CourseService courseService;
    private final UserService userService;
    private User currentTeacher;

    public TeacherStudentViewController() {
        this.courseService = CourseService.getInstance();
        this.userService = UserService.getInstance();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupCourseComboBox();
        courseComboBox.setOnAction(e -> loadStudentsForCourse());
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        emailColumn.setCellValueFactory(data -> data.getValue().emailProperty());
        
        enrollmentDateColumn.setCellValueFactory(data -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> data.getValue().getEnrollmentDate().format(formatter)
            );
        });
        
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
    }

    private void setupCourseComboBox() {
        courseComboBox.setCellFactory(param -> new ListCell<Course>() {
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
        
        courseComboBox.setButtonCell(new ListCell<Course>() {
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
    }

    public void setCurrentTeacher(User teacher) {
        this.currentTeacher = teacher;
        loadTeacherCourses();
    }

    private void loadTeacherCourses() {
        try {
            var courses = courseService.getCoursesByTeacher(currentTeacher.getId());
            courseComboBox.setItems(FXCollections.observableArrayList(courses));
            
            if (!courses.isEmpty()) {
                courseComboBox.getSelectionModel().selectFirst();
                loadStudentsForCourse();
            }
        } catch (Exception e) {
            showError("Error", "Failed to load courses: " + e.getMessage());
        }
    }

    private void loadStudentsForCourse() {
        Course selectedCourse = courseComboBox.getValue();
        if (selectedCourse == null) return;

        try {
            var students = courseService.getEnrolledStudents(selectedCourse.getId());
            studentTable.setItems(FXCollections.observableArrayList(students));
        } catch (Exception e) {
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