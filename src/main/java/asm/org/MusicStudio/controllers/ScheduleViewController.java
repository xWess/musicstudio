package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.services.ScheduleService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;

public class ScheduleViewController {
    @FXML private DatePicker scheduleDatePicker;
    @FXML private ComboBox<String> scheduleViewType;
    @FXML private TableView<Schedule> scheduleTable;
    @FXML private TableColumn<Schedule, String> timeColumn;
    @FXML private TableColumn<Schedule, String> courseColumn;
    @FXML private TableColumn<Schedule, String> teacherColumn;
    @FXML private TableColumn<Schedule, String> roomColumn;
    @FXML private TableColumn<Schedule, String> statusColumn;

    private ScheduleService scheduleService;
    private ObservableList<Schedule> scheduleData;

    @FXML
    public void initialize() {
        // Initialize services
        scheduleService = new ScheduleService();
        scheduleData = FXCollections.observableArrayList();

        // Setup date picker
        scheduleDatePicker.setValue(LocalDate.now());
        scheduleDatePicker.setOnAction(e -> loadSchedule());

        // Setup view type combo
        scheduleViewType.setItems(FXCollections.observableArrayList(
            "Daily", "Weekly", "Monthly"
        ));
        scheduleViewType.setValue("Daily");
        scheduleViewType.setOnAction(e -> loadSchedule());

        // Setup table columns
        setupTableColumns();

        // Load initial data
        loadSchedule();
    }

    private void setupTableColumns() {
        timeColumn.setCellValueFactory(cellData -> 
            cellData.getValue().timeProperty());
        courseColumn.setCellValueFactory(cellData -> 
            cellData.getValue().courseProperty());
        teacherColumn.setCellValueFactory(cellData -> 
            cellData.getValue().teacherProperty());
        roomColumn.setCellValueFactory(cellData -> 
            cellData.getValue().roomProperty());
        statusColumn.setCellValueFactory(cellData -> 
            cellData.getValue().statusProperty());
    }

    private void loadSchedule() {
        try {
            LocalDate selectedDate = scheduleDatePicker.getValue();
            String viewType = scheduleViewType.getValue();
            
            scheduleData.clear();
            List<Schedule> schedules = scheduleService.getSchedule(selectedDate, viewType);
            if (schedules.isEmpty()) {
                showMessage("No schedules found for the selected period");
            } else {
                scheduleData.addAll(schedules);
                scheduleTable.setItems(scheduleData);
            }
            
        } catch (Exception e) {
            showError("Error loading schedule", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 