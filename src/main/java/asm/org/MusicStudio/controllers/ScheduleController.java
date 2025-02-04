package asm.org.MusicStudio.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.ScheduleService;

public class ScheduleController {
    @FXML
    private TableView<Schedule> scheduleTable;
    private ScheduleService scheduleService;
    private User currentUser;

    @FXML
    public void initialize() {
        if (scheduleTable != null) {
            setupTableColumns();
        }
    }

    private void setupTableColumns() {
        // Create and set up columns
        TableColumn<Schedule, LocalDateTime> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        
        TableColumn<Schedule, String> roomColumn = new TableColumn<>("Room");
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("room"));
        
        TableColumn<Schedule, String> teacherColumn = new TableColumn<>("Teacher");
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        
        TableColumn<Schedule, String> studentColumn = new TableColumn<>("Student");
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("student"));

        // Add columns to table
        scheduleTable.getColumns().addAll(dateColumn, roomColumn, teacherColumn, studentColumn);
        
        // Initialize with empty list to avoid null pointer
        scheduleTable.setItems(FXCollections.observableArrayList());
    }

    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
        loadSchedule(); // Load schedule after service is set
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadSchedule(); // Reload schedule when user is set
    }

    public void loadSchedule() {
        if (scheduleTable == null || scheduleService == null || currentUser == null) {
            return; // Exit if not all dependencies are initialized
        }

        try {
            List<Schedule> schedules;
            
            // Load appropriate schedules based on user role
            switch (currentUser.getRole()) {
                case ADMIN:
                    schedules = scheduleService.getAllSchedules();
                    break;
                case TEACHER:
                    schedules = scheduleService.getSchedulesByTeacher(currentUser.getId());
                    break;
                case STUDENT:
                    schedules = scheduleService.getSchedulesByStudent(currentUser.getId());
                    break;
                default:
                    schedules = new ArrayList<>(); // Empty list for other roles
            }

            // Set items to empty list if null
            if (schedules == null) {
                schedules = new ArrayList<>();
            }

            scheduleTable.setItems(FXCollections.observableArrayList(schedules));
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load schedule: " + e.getMessage());
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