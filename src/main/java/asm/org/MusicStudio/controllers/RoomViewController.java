package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.services.RoomService;
import asm.org.MusicStudio.services.RoomServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import asm.org.MusicStudio.dialogs.RoomBookingDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class RoomViewController {
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, Integer> capacityColumn;
    @FXML private TableColumn<Room, String> availabilityColumn;
    @FXML private TableColumn<Room, String> equipmentColumn;
    @FXML private DatePicker roomDatePicker;

    private RoomService roomService;
    private ObservableList<Room> roomData;

    @FXML
    public void initialize() {
        // Initialize services
        roomService = new RoomServiceImpl();
        roomData = FXCollections.observableArrayList();

        // Setup date picker
        roomDatePicker.setValue(LocalDate.now());
        roomDatePicker.setOnAction(e -> loadRooms());

        // Setup table columns
        setupTableColumns();

        // Load initial data
        loadRooms();
    }

    @FXML
    public void showRoomBookingDialog() {
        try {
            Dialog<Room> dialog = new RoomBookingDialog(roomDatePicker.getValue());
            dialog.showAndWait().ifPresent(room -> {
                // Get the current logged-in artist (you'll need to implement this)
                Artist currentArtist = getCurrentArtist(); 
                
                // Get the schedule from the room
                Schedule schedule = room.getSchedules().iterator().next();
                
                roomService.bookRoom(
                    currentArtist,
                    room,
                    schedule.getDate(),
                    LocalTime.parse(schedule.getTime()), // Start time
                    LocalTime.parse(schedule.getTime()).plusHours(1) // End time (assuming 1-hour slots)
                );
                loadRooms();
            });
        } catch (Exception e) {
            showError("Error", "Failed to show booking dialog: " + e.getMessage());
        }
    }

    // Helper method to get current logged-in artist
    private Artist getCurrentArtist() {
        // TODO: Implement this to get the current logged-in artist
        // This should come from your authentication/session management
        throw new UnsupportedOperationException(
            "getCurrentArtist() needs to be implemented"
        );
    }

    private void setupTableColumns() {
        roomNumberColumn.setCellValueFactory(cellData -> 
            cellData.getValue().roomNumberProperty());
        roomTypeColumn.setCellValueFactory(cellData -> 
            cellData.getValue().roomTypeProperty());
        capacityColumn.setCellValueFactory(cellData -> 
            cellData.getValue().capacityProperty().asObject());
        availabilityColumn.setCellValueFactory(cellData -> 
            cellData.getValue().availabilityProperty());
        equipmentColumn.setCellValueFactory(cellData -> 
            cellData.getValue().equipmentProperty());
    }

    private void loadRooms() {
        try {
            LocalDate selectedDate = roomDatePicker.getValue();
            List<Room> availableRooms = roomService.getAvailableRooms(selectedDate);
            roomData.clear();
            roomData.addAll(availableRooms);
            roomsTable.setItems(roomData);
        } catch (Exception e) {
            showError("Error", "Failed to load rooms: " + e.getMessage());
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