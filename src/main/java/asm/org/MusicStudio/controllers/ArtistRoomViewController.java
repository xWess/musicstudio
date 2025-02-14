package asm.org.MusicStudio.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.services.ArtistRoomService;
import asm.org.MusicStudio.services.ArtistRoomServiceImpl;
import asm.org.MusicStudio.services.ArtistSessionService;
import asm.org.MusicStudio.services.ArtistSessionServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import java.sql.SQLException;
import asm.org.MusicStudio.dialogs.RoomPaymentDialog;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.services.PaymentService;

public class ArtistRoomViewController {
    @FXML private TableView<Room> artistRoomsTable;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, Integer> capacityColumn;
    @FXML private TableColumn<Room, String> availabilityColumn;
    @FXML private TableColumn<Room, String> bookedByColumn;
    @FXML private TableColumn<Room, String> timeColumn;
    @FXML private DatePicker artistRoomDatePicker;

    private final ArtistRoomService artistRoomService = new ArtistRoomServiceImpl();
    private ObservableList<Room> roomData;
    private final ArtistSessionService artistSessionService = ArtistSessionServiceImpl.getInstance();

    // Add dialog field
    private Dialog<ButtonType> currentDialog;
    private TableView<Room> availableRoomsTable;

    @FXML
    public void initialize() {
        roomData = FXCollections.observableArrayList();
        
        artistRoomDatePicker.setValue(LocalDate.now());
        artistRoomDatePicker.setOnAction(e -> loadArtistRooms());
        
        setupArtistTableColumns();
        loadArtistRooms();
    }

    private void setupArtistTableColumns() {
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        availabilityColumn.setCellValueFactory(cellData -> {
            Room room = cellData.getValue();
            String status = room.getAvailability();
            return new SimpleStringProperty(status != null ? status : "Available");
        });
        
        bookedByColumn.setCellValueFactory(cellData -> {
            Room room = cellData.getValue();
            return new SimpleStringProperty(room.getBookedByName());
        });
        
        timeColumn.setCellValueFactory(cellData -> {
            Room room = cellData.getValue();
            String bookingTime = room.getBookingTime();
            return new SimpleStringProperty(bookingTime);
        });
        
        artistRoomsTable.setItems(roomData);
    }

    private void loadArtistRooms() {
        try {
            LocalDate selectedDate = artistRoomDatePicker.getValue();
            List<Room> rooms = artistRoomService.getAllRooms(selectedDate);
            roomData.clear();
            roomData.addAll(rooms);
            artistRoomsTable.refresh();
        } catch (Exception e) {
            showError("Error", "Failed to load rooms: " + e.getMessage());
        }
    }

    @FXML
    public void handleBookRoom() {
        try {
            currentDialog = new Dialog<>();
            currentDialog.setTitle("Book Room");
            currentDialog.setHeaderText("Select Booking Time");
            
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            
            DatePicker bookingDatePicker = new DatePicker(artistRoomDatePicker.getValue());
            ComboBox<LocalTime> timeCombo = new ComboBox<>();
            LocalTime time = LocalTime.of(9, 0);
            while (time.isBefore(LocalTime.of(21, 0))) {
                timeCombo.getItems().add(time);
                time = time.plusHours(1);
            }

            // Add listeners to update table when date/time changes
            bookingDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> 
                loadAvailableRooms(newVal, timeCombo.getValue()));
            timeCombo.valueProperty().addListener((obs, oldVal, newVal) -> 
                loadAvailableRooms(bookingDatePicker.getValue(), newVal));
            
            content.getChildren().addAll(
                new Label("Date:"),
                bookingDatePicker,
                new Label("Start Time:"),
                timeCombo
            );
            
            // Add table to show available rooms
            availableRoomsTable = new TableView<>();
            setupAvailableRoomsTable(availableRoomsTable);
            content.getChildren().add(availableRoomsTable);
            
            currentDialog.getDialogPane().setContent(content);
            currentDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            // Disable OK button until a room is selected
            Node okButton = currentDialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.setDisable(true);
            availableRoomsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> okButton.setDisable(newVal == null));
            
            Optional<ButtonType> result = currentDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                LocalDate date = bookingDatePicker.getValue();
                LocalTime startTime = timeCombo.getValue();
                Room selectedRoom = availableRoomsTable.getSelectionModel().getSelectedItem();
                
                if (startTime == null || selectedRoom == null) {
                    showError("Error", "Please select both time and room");
                    return;
                }

                // Show payment dialog with user ID
                Artist currentArtist = artistSessionService.getCurrentArtist();
                if (currentArtist == null) {
                    throw new IllegalStateException("No artist currently logged in");
                }
                
                RoomPaymentDialog paymentDialog = new RoomPaymentDialog(selectedRoom, date, currentArtist.getId());
                Optional<Payment> paymentResult = paymentDialog.showAndWait();
                
                if (paymentResult.isPresent()) {
                    Payment payment = paymentResult.get();
                    LocalTime endTime = startTime.plusHours(1);
                    
                    // First book the room to get the booking ID
                    Integer bookingId = artistRoomService.bookRoom(currentArtist, selectedRoom, date, startTime, endTime);
                    
                    // Then save payment with the booking ID
                    PaymentService paymentService = new PaymentService();
                    paymentService.saveRoomBookingPayment(payment, bookingId);
                    
                    showSuccess("Success", "Room booked and payment completed successfully!");
                    loadArtistRooms();
                }
            }
        } catch (Exception e) {
            showError("Error", e.getMessage());
        } finally {
            currentDialog = null;
            availableRoomsTable = null;
        }
    }

    private void setupAvailableRoomsTable(TableView<Room> table) {
        TableColumn<Room, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        TableColumn<Room, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        
        table.getColumns().addAll(roomCol, typeCol, capacityCol);
        table.setItems(FXCollections.observableArrayList());
    }

    private void loadAvailableRooms(LocalDate date, LocalTime time) {
        if (date == null || time == null || availableRoomsTable == null) return;
        
        try {
            List<Room> rooms = artistRoomService.getAvailableRooms(date, time);
            availableRoomsTable.setItems(FXCollections.observableArrayList(rooms));
        } catch (SQLException e) {
            showError("Error", "Failed to load available rooms: " + e.getMessage());
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