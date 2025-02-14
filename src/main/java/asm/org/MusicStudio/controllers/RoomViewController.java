package asm.org.MusicStudio.controllers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import asm.org.MusicStudio.dialogs.RoomBookingDialog;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.services.ArtistRoomBookingService;
import asm.org.MusicStudio.services.ArtistRoomBookingServiceImpl;
import asm.org.MusicStudio.services.ArtistSessionService;
import asm.org.MusicStudio.services.ArtistSessionServiceImpl;
import asm.org.MusicStudio.services.RoomService;
import asm.org.MusicStudio.services.RoomServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private final ArtistRoomBookingService bookingService = ArtistRoomBookingServiceImpl.getInstance();

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
                try {
                    Schedule schedule = room.getSchedules().iterator().next();
                    bookingService.bookRoom(
                        room,
                        schedule.getDate(),
                        schedule.getStartTime().toLocalTime(),
                        schedule.getEndTime().toLocalTime()
                    );
                    loadRooms();
                    showSuccess("Success", "Room booked successfully!");
                } catch (Exception e) {
                    showError("Booking Error", e.getMessage());
                }
            });
        } catch (Exception e) {
            showError("Error", "Failed to show booking dialog: " + e.getMessage());
        }
    }

    // Helper method to get current logged-in artist
    private Artist getCurrentArtist() {
        ArtistSessionService artistService = ArtistSessionServiceImpl.getInstance();
        Artist currentArtist = artistService.getCurrentArtist();
        if (currentArtist == null) {
            throw new IllegalStateException("No artist currently logged in");
        }
        return currentArtist;
    }

    @FXML
    private void setupTableColumns() {
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        equipmentColumn.setCellValueFactory(new PropertyValueFactory<>("equipment"));
        
        // Add price column
        TableColumn<Room, BigDecimal> priceColumn = new TableColumn<>("Price ($)");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setPrefWidth(100);
        
        availabilityColumn.setCellValueFactory(cellData -> {
            Room room = cellData.getValue();
            return new SimpleStringProperty(room.isAvailable(roomDatePicker.getValue(), 
                LocalTime.now().toString()) ? "Available" : "Booked");
        });
        
        // Add price column to the table
        roomsTable.getColumns().add(priceColumn);
        roomsTable.setItems(roomData);
    }

    private void loadRooms() {
        try {
            LocalDate selectedDate = roomDatePicker.getValue();
            LocalTime selectedTime = LocalTime.now();
            List<Room> availableRooms = roomService.getAvailableRooms(selectedDate, selectedTime);
            roomData.clear();
            roomData.addAll(availableRooms);
            roomsTable.setItems(roomData);
        } catch (SQLException e) {
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

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 