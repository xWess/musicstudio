package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.entity.Teacher;
import asm.org.MusicStudio.services.CourseService;
import asm.org.MusicStudio.services.UserService;
import asm.org.MusicStudio.services.UserServiceImpl;
import asm.org.MusicStudio.services.RoomService;
import asm.org.MusicStudio.services.RoomServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.LocalDate;

public class CourseViewController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> instructorFilter;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> nameColumn;
    @FXML private TableColumn<Course, String> instructorColumn;
    @FXML private TableColumn<Course, String> scheduleColumn;
    @FXML private TableColumn<Course, Double> feeColumn;
    @FXML private TableColumn<Course, Integer> capacityColumn;
    @FXML private TableColumn<Course, String> roomColumn;
    @FXML private VBox courseDetailsPane;
    @FXML private Label courseTitle;
    @FXML private Button addCourseButton;
    @FXML private Button deleteCourseButton;
    
    private CourseService courseService;
    private UserService userService;
    private RoomService roomService;
    private ObservableList<Course> courseList;
    private FilteredList<Course> filteredCourses;
    private User currentUser;
    
    @FXML
    public void initialize() {
        courseService = new CourseService();
        userService = new UserServiceImpl();
        roomService = new RoomServiceImpl();
        setupTable();
        setupSearch();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            loadCourses();
            
            // Show/hide add course button based on role
            boolean isAdminOrTeacher = user.getRole() == Role.ADMIN || user.getRole() == Role.TEACHER;
            if (addCourseButton != null) {
                addCourseButton.setVisible(isAdminOrTeacher);
                addCourseButton.setManaged(isAdminOrTeacher);
            }
        }
    }
    
    private void showAccessDeniedMessage() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access Denied");
        alert.setHeaderText(null);
        alert.setContentText("Course viewing is only available for students.");
        alert.showAndWait();
    }
    
    @FXML
    private void setupTable() {
        nameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getName()));
        
        instructorColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getInstructor()));
        
        scheduleColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            if (course.getSchedules() == null || course.getSchedules().isEmpty()) {
                return new SimpleStringProperty("No schedule");
            }
            
            Schedule schedule = course.getSchedules().get(0);
            String scheduleText = String.format("%s %s-%s", 
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime());
                
            String roomText = schedule.getRoom() != null ? 
                " - Room: " + schedule.getRoom().getLocation() : "";
                
            return new SimpleStringProperty(scheduleText + roomText);
        });
        
        feeColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getMonthlyFee()));
        
        capacityColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getMaxStudents()));
        
        roomColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            if (course.getSchedules() == null || course.getSchedules().isEmpty()) {
                return new SimpleStringProperty("No room assigned");
            }
            Schedule schedule = course.getSchedules().get(0);
            return new SimpleStringProperty(schedule.getRoom() != null ? 
                schedule.getRoom().getLocation() : "No room assigned");
        });
        
        courseTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    displayCourseDetails(newSelection);
                }
            });
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
            showError("Error", "Failed to load courses: " + e.getMessage());
        }
    }
    private void displayCourseDetails(Course course) {
        courseDetailsPane.setVisible(true);
        courseTitle.setText(course.getName());
    }

    public void refreshView() {
        if (currentUser != null && currentUser.getRole() == Role.STUDENT) {
            loadCourses();
            courseDetailsPane.setVisible(false);
            searchField.clear();
            instructorFilter.getSelectionModel().clearSelection();
        }
    }

    @FXML
    public void showAddCourseDialog() {
        try {
            if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.TEACHER)) {
                showError("Access Denied", "Only teachers and administrators can add courses.");
                return;
            }

            Dialog<Course> dialog = new Dialog<>();
            dialog.setTitle("Add New Course");
            dialog.setHeaderText("Enter Course Details");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField nameField = new TextField();
            TextField descriptionField = new TextField();
            TextField feeField = new TextField();
            TextField maxStudentsField = new TextField();
            
            // Add teacher selection for admin users
            ComboBox<Teacher> teacherCombo = new ComboBox<>();
            if (currentUser.getRole() == Role.ADMIN) {
                List<Teacher> teachers = userService.getAllTeachers();
                teacherCombo.setItems(FXCollections.observableArrayList(teachers));
                teacherCombo.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Teacher teacher) {
                        return teacher != null ? teacher.getName() : "";
                    }

                    @Override
                    public Teacher fromString(String string) {
                        if (string == null || string.isEmpty()) return null;
                        return teacherCombo.getItems().stream()
                            .filter(teacher -> teacher.getName().equals(string))
                            .findFirst()
                            .orElse(null);
                    }
                });
            }

            // Schedule input fields
            ComboBox<String> dayOfWeekCombo = new ComboBox<>();
            dayOfWeekCombo.getItems().addAll("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");
            
            ComboBox<Room> roomCombo = new ComboBox<>();
            List<Room> availableRooms = roomService.getAvailableRooms(LocalDate.now());
            roomCombo.setItems(FXCollections.observableArrayList(availableRooms));
            roomCombo.setConverter(new StringConverter<>() {
                @Override
                public String toString(Room room) {
                    if (room == null) return "";
                    return String.format("Room %s - %s (Capacity: %d)", 
                        room.getRoomNumber(), 
                        room.getLocation(), 
                        room.getCapacity());
                }

                @Override
                public Room fromString(String string) {
                    return null; // Not needed for ComboBox
                }
            });

            TextField startTimeField = new TextField();
            TextField endTimeField = new TextField();

            grid.add(new Label("Course Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descriptionField, 1, 1);
            
            // Add teacher selection for admin users
            if (currentUser.getRole() == Role.ADMIN) {
                grid.add(new Label("Teacher:"), 0, 2);
                grid.add(teacherCombo, 1, 2);
                // Shift other fields down by 1
                grid.add(new Label("Monthly Fee:"), 0, 3);
                grid.add(feeField, 1, 3);
                grid.add(new Label("Max Students:"), 0, 4);
                grid.add(maxStudentsField, 1, 4);
            } else {
                grid.add(new Label("Monthly Fee:"), 0, 2);
                grid.add(feeField, 1, 2);
                grid.add(new Label("Max Students:"), 0, 3);
                grid.add(maxStudentsField, 1, 3);
            }
            grid.add(new Label("Day of Week:"), 0, 5);
            grid.add(dayOfWeekCombo, 1, 5);
            grid.add(new Label("Room:"), 0, 6);
            grid.add(roomCombo, 1, 6);
            grid.add(new Label("Start Time (HH:mm):"), 0, 7);
            grid.add(startTimeField, 1, 7);
            grid.add(new Label("End Time (HH:mm):"), 0, 8);
            grid.add(endTimeField, 1, 8);

            dialog.getDialogPane().setContent(grid);
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    try {
                        String instructor = currentUser.getRole() == Role.ADMIN && teacherCombo.getValue() != null 
                            ? teacherCombo.getValue().getName() 
                            : currentUser.getName();

                        Schedule schedule = Schedule.builder()
                            .dayOfWeek(dayOfWeekCombo.getValue())
                            .room(roomCombo.getValue())
                            .startTime(LocalTime.parse(startTimeField.getText()))
                            .endTime(LocalTime.parse(endTimeField.getText()))
                            .status("ACTIVE")
                            .build();

                        List<Schedule> schedules = new ArrayList<>();
                        schedules.add(schedule);

                        return Course.builder()
                            .name(nameField.getText())
                            .description(descriptionField.getText())
                            .monthlyFee(Double.parseDouble(feeField.getText()))
                            .maxStudents(Integer.parseInt(maxStudentsField.getText()))
                            .instructor(instructor)
                            .schedules(schedules)
                            .build();
                    } catch (Exception e) {
                        showError("Invalid Input", "Please check your input values: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            // Add listeners for validation
            nameField.textProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            descriptionField.textProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            feeField.textProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            maxStudentsField.textProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            dayOfWeekCombo.valueProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            roomCombo.valueProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            startTimeField.textProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            endTimeField.textProperty().addListener((obs, old, newVal) -> 
                validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                    maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            if (currentUser.getRole() == Role.ADMIN) {
                teacherCombo.valueProperty().addListener((obs, old, newVal) -> 
                    validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                        maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo));
            }

            // Initial validation state
            validateAddCourseForm(dialog, addButtonType, nameField, descriptionField, feeField, 
                maxStudentsField, dayOfWeekCombo, roomCombo, startTimeField, endTimeField, teacherCombo);

            dialog.showAndWait().ifPresent(course -> {
                try {
                    courseService.addCourse(course);
                    loadCourses();
                    showSuccess("Success", "Course added successfully!");
                } catch (Exception e) {
                    showError("Error", "Failed to add course: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showError("Error", "Failed to show add course dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showError("No Selection", "Please select a course to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Course");
        alert.setHeaderText("Delete Course Confirmation");
        alert.setContentText("Are you sure you want to delete this course?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    courseService.deleteCourse(selectedCourse.getId());
                    loadCourses();
                    showSuccess("Success", "Course deleted successfully");
                } catch (Exception e) {
                    showError("Error", "Failed to delete course: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void showEditCourseDialog() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showError("No Selection", "Please select a course to edit");
            return;
        }

        try {
            Dialog<Course> dialog = new Dialog<>();
            dialog.setTitle("Edit Course");
            dialog.setHeaderText("Edit Course Details");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Create form fields
            TextField nameField = new TextField(selectedCourse.getName());
            nameField.setPromptText("Course Name");

            TextField descriptionField = new TextField(selectedCourse.getDescription());
            descriptionField.setPromptText("Course Description");

            TextField feeField = new TextField(String.valueOf(selectedCourse.getMonthlyFee()));
            feeField.setPromptText("Monthly Fee");

            TextField maxStudentsField = new TextField(String.valueOf(selectedCourse.getMaxStudents()));
            maxStudentsField.setPromptText("Maximum Students");

            // Teacher selection (for admin only)
            ComboBox<Teacher> teacherCombo = new ComboBox<>();
            if (currentUser.getRole() == Role.ADMIN) {
                List<Teacher> teachers = userService.getAllTeachers();
                teacherCombo.setItems(FXCollections.observableArrayList(teachers));
                teacherCombo.setValue(teachers.stream()
                    .filter(t -> t.getName().equals(selectedCourse.getInstructor()))
                    .findFirst()
                    .orElse(null));
                teacherCombo.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Teacher teacher) {
                        return teacher != null ? teacher.getName() : "";
                    }

                    @Override
                    public Teacher fromString(String string) {
                        return null;
                    }
                });
            }

            // Schedule Table
            TableView<Schedule> scheduleTable = new TableView<>();
            scheduleTable.setPrefHeight(200);
            
            TableColumn<Schedule, String> dayColumn = new TableColumn<>("Day");
            dayColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getDayOfWeek()));
            
            TableColumn<Schedule, String> timeColumn = new TableColumn<>("Time");
            timeColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getStartTime() + " - " + 
                                       cellData.getValue().getEndTime()));
            
            TableColumn<Schedule, String> roomColumn = new TableColumn<>("Room");
            roomColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getRoom() != null ? 
                    cellData.getValue().getRoom().getLocation() : ""));
            
            scheduleTable.getColumns().addAll(dayColumn, timeColumn, roomColumn);
            
            // Add existing schedules to the table
            ObservableList<Schedule> schedules = FXCollections.observableArrayList(
                selectedCourse.getSchedules());
            scheduleTable.setItems(schedules);

            // Schedule management buttons
            Button addScheduleBtn = new Button("Add Schedule");
            Button editScheduleBtn = new Button("Edit Schedule");
            Button deleteScheduleBtn = new Button("Delete Schedule");
            
            HBox scheduleButtons = new HBox(10, addScheduleBtn, editScheduleBtn, deleteScheduleBtn);

            // Add fields to grid
            int row = 0;
            grid.add(new Label("Course Name:"), 0, row);
            grid.add(nameField, 1, row++);

            grid.add(new Label("Description:"), 0, row);
            grid.add(descriptionField, 1, row++);

            if (currentUser.getRole() == Role.ADMIN) {
                grid.add(new Label("Teacher:"), 0, row);
                grid.add(teacherCombo, 1, row++);
            }

            grid.add(new Label("Monthly Fee:"), 0, row);
            grid.add(feeField, 1, row++);

            grid.add(new Label("Maximum Students:"), 0, row);
            grid.add(maxStudentsField, 1, row++);

            grid.add(new Label("Schedules:"), 0, row);
            grid.add(scheduleTable, 1, row++);
            grid.add(scheduleButtons, 1, row++);

            // Schedule button actions
            addScheduleBtn.setOnAction(e -> {
                Dialog<Schedule> scheduleDialog = createScheduleDialog(null, selectedCourse);
                scheduleDialog.showAndWait().ifPresent(newSchedule -> {
                    schedules.add(newSchedule);
                });
            });
            
            editScheduleBtn.setOnAction(e -> {
                Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
                if (selectedSchedule != null) {
                    Dialog<Schedule> scheduleDialog = createScheduleDialog(selectedSchedule, selectedCourse);
                    scheduleDialog.showAndWait().ifPresent(updatedSchedule -> {
                        int index = schedules.indexOf(selectedSchedule);
                        schedules.set(index, updatedSchedule);
                    });
                } else {
                    showError("No Selection", "Please select a schedule to edit");
                }
            });
            
            deleteScheduleBtn.setOnAction(e -> {
                Schedule selectedSchedule = scheduleTable.getSelectionModel().getSelectedItem();
                if (selectedSchedule != null) {
                    schedules.remove(selectedSchedule);
                } else {
                    showError("No Selection", "Please select a schedule to delete");
                }
            });

            dialog.getDialogPane().setContent(grid);

            // Add buttons
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Enable/Disable save button based on validation
            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(false);

            // Form validation
            nameField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveButton.setDisable(newValue.trim().isEmpty());
            });

            // Convert dialog result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        // Update course with new values
                        selectedCourse.setName(nameField.getText().trim());
                        selectedCourse.setDescription(descriptionField.getText().trim());
                        selectedCourse.setMonthlyFee(Double.parseDouble(feeField.getText().trim()));
                        selectedCourse.setMaxStudents(Integer.parseInt(maxStudentsField.getText().trim()));
                        
                        if (currentUser.getRole() == Role.ADMIN && teacherCombo.getValue() != null) {
                            selectedCourse.setInstructor(teacherCombo.getValue().getName());
                        }
                        
                        // Update schedules
                        selectedCourse.setSchedules(new ArrayList<>(schedules));
                        
                        return selectedCourse;
                    } catch (NumberFormatException e) {
                        showError("Invalid Input", "Please enter valid numbers for fee and maximum students");
                        return null;
                    }
                }
                return null;
            });

            // Show dialog and handle result
            dialog.showAndWait().ifPresent(updatedCourse -> {
                try {
                    courseService.updateCourse(updatedCourse);
                    loadCourses(); // Refresh the course list
                    courseTable.refresh(); // Add this line to force refresh the table view
                    showSuccess("Success", "Course updated successfully!");
                } catch (SQLException e) {
                    showError("Error", "Failed to update course: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            showError("Error", "Failed to show edit dialog: " + e.getMessage());
        }
    }

    // Helper method to create schedule dialog
    private Dialog<Schedule> createScheduleDialog(Schedule existingSchedule, Course course) {
        Dialog<Schedule> dialog = new Dialog<>();
        dialog.setTitle(existingSchedule == null ? "Add Schedule" : "Edit Schedule");
        dialog.setHeaderText(existingSchedule == null ? "Add New Schedule" : "Edit Schedule");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Day of week selection
        ComboBox<String> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY");
        if (existingSchedule != null) {
            dayCombo.setValue(existingSchedule.getDayOfWeek());
        }

        // Time inputs
        TextField startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm");
        if (existingSchedule != null) {
            startTimeField.setText(existingSchedule.getStartTime().toString());
        }

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm");
        if (existingSchedule != null) {
            endTimeField.setText(existingSchedule.getEndTime().toString());
        }

        // Room selection
        ComboBox<Room> roomCombo = new ComboBox<>();
        try {
            List<Room> rooms = roomService.getAvailableRooms(LocalDate.now());
            roomCombo.setItems(FXCollections.observableArrayList(rooms));
            if (existingSchedule != null && existingSchedule.getRoom() != null) {
                roomCombo.setValue(existingSchedule.getRoom());
            }
        } catch (SQLException e) {
            showError("Error", "Failed to load rooms: " + e.getMessage());
        }

        roomCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Room room) {
                return room != null ? room.getLocation() + " (Capacity: " + room.getCapacity() + ")" : "";
            }

            @Override
            public Room fromString(String string) {
                return null;
            }
        });

        // Add fields to grid
        grid.add(new Label("Day:"), 0, 0);
        grid.add(dayCombo, 1, 0);
        grid.add(new Label("Start Time:"), 0, 1);
        grid.add(startTimeField, 1, 1);
        grid.add(new Label("End Time:"), 0, 2);
        grid.add(endTimeField, 1, 2);
        grid.add(new Label("Room:"), 0, 3);
        grid.add(roomCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return Schedule.builder()
                        .scheduleId(existingSchedule != null ? existingSchedule.getScheduleId() : null)
                        .dayOfWeek(dayCombo.getValue())
                        .startTime(LocalTime.parse(startTimeField.getText()))
                        .endTime(LocalTime.parse(endTimeField.getText()))
                        .room(roomCombo.getValue())
                        .course(course)
                        .status("ACTIVE")
                        .build();
                } catch (Exception e) {
                    showError("Invalid Input", "Please check your input values: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void validateAddCourseForm(Dialog<?> dialog, ButtonType addButtonType, TextField nameField, 
            TextField descriptionField, TextField feeField, TextField maxStudentsField,
            ComboBox<String> dayOfWeekCombo, ComboBox<Room> roomCombo,
            TextField startTimeField, TextField endTimeField, ComboBox<Teacher> teacherCombo) {
                
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        
        // Basic field validation
        boolean isValid = !nameField.getText().trim().isEmpty() 
                && !descriptionField.getText().trim().isEmpty()
                && !feeField.getText().trim().isEmpty()
                && !maxStudentsField.getText().trim().isEmpty()
                && dayOfWeekCombo.getValue() != null
                && roomCombo.getValue() != null
                && !startTimeField.getText().trim().isEmpty()
                && !endTimeField.getText().trim().isEmpty();
                
        // Additional validation for admin users
        if (currentUser.getRole() == Role.ADMIN) {
            isValid = isValid && teacherCombo.getValue() != null;
        }
        
        // Validate fee is a positive number
        try {
            if (!feeField.getText().trim().isEmpty()) {
                double fee = Double.parseDouble(feeField.getText().trim());
                isValid = isValid && fee > 0;
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }
        
        // Validate max students is a positive integer
        try {
            if (!maxStudentsField.getText().trim().isEmpty()) {
                int maxStudents = Integer.parseInt(maxStudentsField.getText().trim());
                isValid = isValid && maxStudents > 0;
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }
        
        // Validate time format and logic
        try {
            if (!startTimeField.getText().trim().isEmpty() && !endTimeField.getText().trim().isEmpty()) {
                LocalTime start = LocalTime.parse(startTimeField.getText().trim());
                LocalTime end = LocalTime.parse(endTimeField.getText().trim());
                isValid = isValid && end.isAfter(start);
            }
        } catch (Exception e) {
            isValid = false;
        }
        
        addButton.setDisable(!isValid);
    }
}