package asm.org.MusicStudio.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import asm.org.MusicStudio.dialogs.BookingHistoryDialog;
import asm.org.MusicStudio.dialogs.RoomBookingDialog;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.services.RoomService;
import asm.org.MusicStudio.services.RoomServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class RoomViewController {
    @FXML private VBox roomsContent;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, Integer> capacityColumn;
    @FXML private TableColumn<Room, String> availabilityColumn;
    @FXML private DatePicker roomDatePicker;

    private RoomService roomService;
    private ObservableList<Room> roomData;

    @FXML
    public void initialize() {
        roomService = new RoomServiceImpl();
        roomData = FXCollections.observableArrayList();
        
        setupTableColumns();
        
        roomDatePicker.setValue(LocalDate.now());
        roomDatePicker.setOnAction(e -> loadRooms());
        
        loadRooms();
    }

    private void setupTableColumns() {
        roomNumberColumn.setCellValueFactory(cellData -> 
            cellData.getValue().displayInfoProperty());
        capacityColumn.setCellValueFactory(cellData -> 
            cellData.getValue().capacityProperty().asObject());
        availabilityColumn.setCellValueFactory(cellData -> 
            cellData.getValue().availabilityProperty());
    }

    private void loadRooms() {
        try {
            List<Room> rooms = roomService.getAvailableRooms(roomDatePicker.getValue());
            roomData.setAll(rooms);
            roomsTable.setItems(roomData);
            roomsTable.refresh(); // Force refresh the table view
        } catch (RuntimeException e) {
            showError("Error", "Failed to load rooms: " + e.getMessage());
        }
    }

    @FXML
    public void showRoomBookingDialog() {
        try {
            Dialog<Room> dialog = new RoomBookingDialog(roomDatePicker.getValue());
            dialog.showAndWait().ifPresent(bookingDetails -> {
                try {
                    String[] timeSlot = bookingDetails.getTimeSlot().split("-");
                    LocalTime startTime = LocalTime.parse(timeSlot[0].trim());
                    LocalTime endTime = LocalTime.parse(timeSlot[1].trim());
                    
                    roomService.bookRoom(bookingDetails.getId(), 1,  // Using default user ID 1
                        bookingDetails.getDate(), startTime, endTime);
                        
                    // Update the room in the table
                    for (Room room : roomData) {
                        if (room.getId() == bookingDetails.getId()) {
                            room.setDate(bookingDetails.getDate());
                            room.setTimeSlot(bookingDetails.getTimeSlot());
                            room.updateAvailabilityProperty(true);
                            break;
                        }
                    }
                    roomsTable.refresh(); // Force refresh the table view
                    showBookingConfirmation(bookingDetails);
                    showSuccess("Success", "Room booked successfully!");
                } catch (Exception e) {
                    showError("Booking Error", e.getMessage());
                }
            });
        } catch (Exception e) {
            showError("Error", "Failed to show booking dialog: " + e.getMessage());
        }
    }

    private void showBookingConfirmation(Room booking) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Confirmation");
        alert.setHeaderText("Booking Details");
        
        String content = String.format("""
            Room: %s
            Date: %s
            Time: %s
            """, 
            booking.getLocation(),
            booking.getDate(),
            booking.getTimeSlot());
            
        alert.setContentText(content);
        alert.showAndWait();
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

    @FXML
    public void showBookingHistory() {
        try {
            List<Room> bookings = roomService.getRoomBookingHistory(1); // Using default user ID 1
            Dialog<Void> dialog = new BookingHistoryDialog(bookings);
            dialog.show();
        } catch (Exception e) {
            showError("Error", "Failed to load booking history: " + e.getMessage());
        }
    }
} 