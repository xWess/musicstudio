package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.services.CourseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.sql.SQLException;
import java.util.List;

public class CourseViewController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> instructorFilter;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> nameColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private TableColumn<Course, Double> feeColumn;
    @FXML private TableColumn<Course, Integer> capacityColumn;
    @FXML private VBox courseDetailsPane;
    @FXML private Label courseTitle;
    @FXML private TextArea courseDescription;
    
    private CourseService courseService;
    private ObservableList<Course> courseList;
    private FilteredList<Course> filteredCourses;
    
    @FXML
    public void initialize() {
        courseService = new CourseService();
        setupTable();
        setupSearch();
        loadCourses();
    }
    
    private void setupTable() {
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        instructorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getInstructor()));
        scheduleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSchedule()));
        feeColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMonthlyFee()));
        capacityColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getMaxStudents()));
        
        courseTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    displayCourseDetails(newVal);
                }
            }
        );
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (filteredCourses != null) {
                filteredCourses.setPredicate(course -> {
                    if (newVal == null || newVal.isEmpty()) {
                        return true;
                    }
                    
                    String lowerCaseFilter = newVal.toLowerCase();
                    return course.getName().toLowerCase().contains(lowerCaseFilter) ||
                           course.getInstructor().toLowerCase().contains(lowerCaseFilter);
                });
            }
        });
    }
    
    private void loadCourses() {
        try {
            List<Course> courses = courseService.getAvailableCourses();
            courseList = FXCollections.observableArrayList(courses);
            filteredCourses = new FilteredList<>(courseList, p -> true);
            courseTable.setItems(filteredCourses);
            
            // Populate instructor filter
            List<String> instructors = courses.stream()
                .map(Course::getInstructor)
                .distinct()
                .sorted()
                .toList();
            instructorFilter.getItems().setAll(instructors);
        } catch (SQLException e) {
            // Handle error appropriately
            e.printStackTrace();
        }
    }
    
    private void displayCourseDetails(Course course) {
        courseDetailsPane.setVisible(true);
        courseTitle.setText(course.getName());
        courseDescription.setText(course.getDescription());
    }

    public void refreshView() {
        loadCourses();
        courseDetailsPane.setVisible(false);
        searchField.clear();
        instructorFilter.getSelectionModel().clearSelection();
    }
}