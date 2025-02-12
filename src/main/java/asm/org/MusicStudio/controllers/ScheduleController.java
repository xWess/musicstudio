package asm.org.MusicStudio.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.ScheduleService;
import java.time.LocalDate;
import asm.org.MusicStudio.entity.Role;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import java.time.LocalTime;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.services.CourseService;
import asm.org.MusicStudio.services.RoomService;
import java.util.stream.Collectors;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ScheduleController {
    @FXML
    private TableView<Schedule> scheduleTable;
    @FXML
    private DatePicker scheduleDatePicker;
    @FXML
    private ComboBox<String> scheduleViewType;
    @FXML
    private TableColumn<Schedule, LocalDateTime> timeColumn;
    @FXML
    private TableColumn<Schedule, String> courseColumn;
    @FXML
    private TableColumn<Schedule, String> teacherColumn;
    @FXML
    private TableColumn<Schedule, String> roomColumn;
    @FXML
    private TableColumn<Schedule, String> statusColumn;
    @FXML
    private HBox adminControls;
    @FXML
    private Button addScheduleButton;
    @FXML
    private Button editScheduleButton;
    @FXML
    private Button deleteScheduleButton;

    private ScheduleService scheduleService;
    private User currentUser;
    private CourseService courseService;
    private RoomService roomService;

    @FXML
    public void initialize() {
        System.out.println("Debug: Initializing ScheduleController");
        
        // Initialize services
        scheduleService = ScheduleService.getInstance();
        
        // Initialize the date picker with current date
        scheduleDatePicker.setValue(LocalDate.now());
        
        // Setup view type combo box
        scheduleViewType.setItems(FXCollections.observableArrayList(
            "Daily", "Weekly", "Monthly"
        ));
        scheduleViewType.setValue("Daily");
        
        // Setup table columns
        timeColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getStartTime().atDate(cellData.getValue().getDate())));
        courseColumn.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        teacherColumn.setCellValueFactory(cellData -> cellData.getValue().teacherProperty());
        roomColumn.setCellValueFactory(cellData -> cellData.getValue().roomProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        // Add listeners for date and view type changes
        scheduleDatePicker.setOnAction(e -> loadSchedule());
        scheduleViewType.setOnAction(e -> loadSchedule());
        
        // Initially hide admin controls
        adminControls.setVisible(false);
        adminControls.setManaged(false);
        
        // Setup admin button handlers
        addScheduleButton.setOnAction(e -> showScheduleDialog(null));
        editScheduleButton.setOnAction(e -> editSelectedSchedule());
        deleteScheduleButton.setOnAction(e -> deleteSelectedSchedule());
        
        // Disable edit/delete buttons when no selection
        editScheduleButton.disableProperty().bind(
            scheduleTable.getSelectionModel().selectedItemProperty().isNull());
        deleteScheduleButton.disableProperty().bind(
            scheduleTable.getSelectionModel().selectedItemProperty().isNull());
            
        System.out.println("Debug: ScheduleController initialization complete");
    }

    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
        loadSchedule(); // Load schedule after service is set
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Show admin controls only for admin users
        boolean isAdmin = user != null && user.getRole() == Role.ADMIN;
        adminControls.setVisible(isAdmin);
        adminControls.setManaged(isAdmin);
        loadSchedule();
    }

    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    private void loadSchedule() {
        if (scheduleTable == null || scheduleService == null || currentUser == null) {
            System.out.println("Debug: Dependencies not initialized:");
            System.out.println("- scheduleTable: " + (scheduleTable == null ? "null" : "ok"));
            System.out.println("- scheduleService: " + (scheduleService == null ? "null" : "ok"));
            System.out.println("- currentUser: " + (currentUser == null ? "null" : "ok"));
            return;
        }

        try {
            List<Schedule> schedules;
            LocalDate selectedDate = scheduleDatePicker.getValue();
            String viewType = scheduleViewType.getValue();
            
            System.out.println("Debug: Loading schedules for date: " + selectedDate);
            System.out.println("Debug: View type: " + viewType);
            System.out.println("Debug: User role: " + currentUser.getRole());
            
            if (currentUser.getRole() == Role.ADMIN) {
                schedules = scheduleService.getAllSchedules();
            } else {
                schedules = scheduleService.getSchedule(selectedDate, viewType);
            }

            System.out.println("Debug: Loaded " + schedules.size() + " schedules");
            scheduleTable.setItems(FXCollections.observableArrayList(schedules));
            
        } catch (Exception e) {
            System.err.println("Debug: Error loading schedules: " + e.getMessage());
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

    private void showScheduleDialog(Schedule schedule) {
        try {
            Dialog<Schedule> dialog = new Dialog<>();
            dialog.setTitle(schedule == null ? "Add New Schedule" : "Edit Schedule");
            
            // Create dialog content
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DatePicker datePicker = new DatePicker();
            ComboBox<String> courseCombo = new ComboBox<>();
            ComboBox<String> roomCombo = new ComboBox<>();
            TextField startTimeField = new TextField();
            TextField endTimeField = new TextField();

            // Populate course combo
            List<Course> courses = courseService.getAllActiveCourses();
            courseCombo.setItems(FXCollections.observableArrayList(
                courses.stream()
                       .map(Course::getName)
                       .collect(Collectors.toList())
            ));
            
            // Populate room combo
            List<Room> rooms = roomService.getAllRooms();
            roomCombo.setItems(FXCollections.observableArrayList(
                rooms.stream()
                     .map(Room::getLocation)
                     .collect(Collectors.toList())
            ));

            // Populate fields if editing
            if (schedule != null) {
                datePicker.setValue(schedule.getDate());
                startTimeField.setText(schedule.getStartTime().toString());
                endTimeField.setText(schedule.getEndTime().toString());
                courseCombo.setValue(schedule.getCourse() != null ? schedule.getCourse().getName() : "");
                roomCombo.setValue(schedule.getRoom() != null ? schedule.getRoom().getLocation() : "");
            }

            // Add fields to grid
            grid.add(new Label("Date:"), 0, 0);
            grid.add(datePicker, 1, 0);
            grid.add(new Label("Course:"), 0, 1);
            grid.add(courseCombo, 1, 1);
            grid.add(new Label("Room:"), 0, 2);
            grid.add(roomCombo, 1, 2);
            grid.add(new Label("Start Time:"), 0, 3);
            grid.add(startTimeField, 1, 3);
            grid.add(new Label("End Time:"), 0, 4);
            grid.add(endTimeField, 1, 4);

            dialog.getDialogPane().setContent(grid);

            // Add buttons
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Handle save action
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        Schedule newSchedule = schedule == null ? new Schedule() : schedule;
                        newSchedule.setDate(datePicker.getValue());
                        
                        String selectedCourse = courseCombo.getValue();
                        if (selectedCourse == null || selectedCourse.isEmpty()) {
                            throw new IllegalArgumentException("Please select a course");
                        }
                        // TODO: Convert string to Course object
                        // newSchedule.setCourse(courseService.getCourseByName(selectedCourse));
                        
                        String selectedRoom = roomCombo.getValue();
                        if (selectedRoom == null || selectedRoom.isEmpty()) {
                            throw new IllegalArgumentException("Please select a room");
                        }
                        // TODO: Convert string to Room object
                        // newSchedule.setRoom(roomService.getRoomByLocation(selectedRoom));
                        
                        // Validate and parse time inputs
                        String startTimeStr = startTimeField.getText().trim();
                        String endTimeStr = endTimeField.getText().trim();
                        if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                            throw new IllegalArgumentException("Start and end times are required");
                        }
                        
                        LocalTime startTime = LocalTime.parse(startTimeStr);
                        LocalTime endTime = LocalTime.parse(endTimeStr);
                        
                        if (endTime.isBefore(startTime)) {
                            throw new IllegalArgumentException("End time cannot be before start time");
                        }
                        
                        newSchedule.setTimeRange(startTime, endTime);
                        newSchedule.setStatus("ACTIVE");
                        newSchedule.setDayOfWeek(datePicker.getValue().getDayOfWeek().toString());
                        
                        return newSchedule;
                    } catch (IllegalArgumentException e) {
                        showError("Invalid Input", e.getMessage());
                        return null;
                    } catch (Exception e) {
                        showError("Error", "An unexpected error occurred: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            // Show dialog and handle result
            dialog.showAndWait().ifPresent(result -> {
                if (schedule == null) {
                    scheduleService.createSchedule(result);
                } else {
                    scheduleService.updateSchedule(result);
                }
                loadSchedule();
            });

        } catch (Exception e) {
            showError("Error", "Failed to show schedule dialog: " + e.getMessage());
        }
    }

    private void editSelectedSchedule() {
        Schedule selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showScheduleDialog(selected);
        }
    }

    private void deleteSelectedSchedule() {
        Schedule selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Schedule");
            alert.setHeaderText("Are you sure you want to delete this schedule?");
            alert.setContentText("This action cannot be undone.");

            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    try {
                        selected.setStatus("CANCELLED");
                        scheduleService.updateSchedule(selected);
                        loadSchedule();
                    } catch (Exception e) {
                        showError("Error", "Failed to delete schedule: " + e.getMessage());
                    }
                }
            });
        }
    }

    // This should be in your main application or navigation controller
    private void initializeScheduleView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScheduleView.fxml"));
            Parent scheduleView = loader.load();
            
            ScheduleController controller = loader.getController();
            controller.setScheduleService(ScheduleService.getInstance());
            controller.setCourseService(CourseService.getInstance());
            controller.setRoomService(RoomService.getInstance());
            controller.setCurrentUser(currentUser); // Set the current user
            
            // Add the view to your scene
            // mainContainer.setCenter(scheduleView); // or however you're displaying it
            
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }
} 