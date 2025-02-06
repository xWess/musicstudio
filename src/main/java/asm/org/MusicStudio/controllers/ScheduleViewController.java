package asm.org.MusicStudio.controllers;

import java.util.List;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.services.CourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ScheduleViewController {
    @FXML private TableView<Course> scheduleTable;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> dayColumn;
    @FXML private TableColumn<Course, String> timeColumn;
    @FXML private TableColumn<Course, String> studentsColumn;
    @FXML private TableColumn<Course, String> roomColumn;
    @FXML private ComboBox<String> dayFilter;
    @FXML private ComboBox<String> timeFilter;

    private CourseService courseService;
    private int currentTeacherId;
    private ObservableList<Course> courseData;

    @FXML
    public void initialize() {
        courseService = new CourseService();
        courseData = FXCollections.observableArrayList();
        
        setupFilters();
        setupTableColumns();
    }

    public void setCurrentTeacherId(int teacherId) {
        System.out.println("Setting teacher ID: " + teacherId);
        this.currentTeacherId = teacherId;
        loadSchedule();
    }

    private void setupFilters() {
        dayFilter.setItems(FXCollections.observableArrayList(
            "All Days", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
        ));
        dayFilter.setValue("All Days");
        
        timeFilter.setItems(FXCollections.observableArrayList(
            "All Times", "Morning", "Afternoon", "Evening"
        ));
        timeFilter.setValue("All Times");
        
        dayFilter.setOnAction(e -> filterSchedule());
        timeFilter.setOnAction(e -> filterSchedule());
    }

    private void setupTableColumns() {
        courseNameColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getName()));
            
        dayColumn.setCellValueFactory(data -> {
            String schedule = data.getValue().getSchedule();
            return new SimpleStringProperty(extractDay(schedule));
        });
        
        timeColumn.setCellValueFactory(data -> {
            String schedule = data.getValue().getSchedule();
            return new SimpleStringProperty(extractTime(schedule));
        });
        
        studentsColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getEnrolledCount() + "/" + 
                                   data.getValue().getMaxStudents()));
                                   
        roomColumn.setCellValueFactory(data -> {
            String schedule = data.getValue().getSchedule();
            return new SimpleStringProperty(extractRoom(schedule));
        });
    }

    private void loadSchedule() {
        try {
            System.out.println("Loading schedule for teacher ID: " + currentTeacherId);
            courseData.clear();
            List<Course> courses = courseService.getCoursesByTeacher(currentTeacherId);
            System.out.println("Found " + courses.size() + " courses");
            courseData.addAll(courses);
            scheduleTable.setItems(courseData);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load schedule: " + e.getMessage());
            showError("Error", "Failed to load schedule: " + e.getMessage());
        }
    }

    private void filterSchedule() {
        String selectedDay = dayFilter.getValue();
        String selectedTime = timeFilter.getValue();
        System.out.println("Filtering - Day: " + selectedDay + ", Time: " + selectedTime);
        
        ObservableList<Course> filteredData = courseData.filtered(course -> {
            boolean dayMatch = selectedDay.equals("All Days") || 
                             extractDay(course.getSchedule()).equals(selectedDay);
            boolean timeMatch = selectedTime.equals("All Times") || 
                              matchesTimeFilter(extractTime(course.getSchedule()), selectedTime);
            return dayMatch && timeMatch;
        });
        
        System.out.println("Filtered from " + courseData.size() + " to " + filteredData.size() + " courses");
        scheduleTable.setItems(filteredData);
    }

    private boolean matchesTimeFilter(String time, String filter) {
        if (filter.equals("All Times")) return true;
        try {
            String timeStr = time.split("-")[0].trim();
            int hour = Integer.parseInt(timeStr.split(":")[0]);
            
            return switch(filter) {
                case "Morning" -> hour >= 8 && hour < 12;
                case "Afternoon" -> hour >= 12 && hour < 17;
                case "Evening" -> hour >= 17 && hour < 22;
                default -> true;
            };
        } catch (Exception e) {
            System.err.println("Error parsing time: " + e.getMessage());
            return false;
        }
    }

    private String extractDay(String schedule) {
        if (schedule == null) return "Not scheduled";
        try {
            return schedule.split(" ")[0];
        } catch (Exception e) {
            return "Invalid schedule";
        }
    }

    private String extractTime(String schedule) {
        if (schedule == null) return "Not set";
        try {
            String[] parts = schedule.split(" ");
            return parts.length > 1 ? parts[1] : "Time not set";
        } catch (Exception e) {
            return "Invalid time";
        }
    }

    private String extractRoom(String schedule) {
        if (schedule == null) return "Not assigned";
        try {
            int start = schedule.indexOf("(Room: ") + 7;
            int end = schedule.indexOf(")", start);
            return start > 6 && end > start ? schedule.substring(start, end) : "Not assigned";
        } catch (Exception e) {
            return "Not assigned";
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