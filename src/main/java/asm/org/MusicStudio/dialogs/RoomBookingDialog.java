package asm.org.MusicStudio.dialogs;

import java.time.LocalDate;
import java.util.List;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.services.RoomService;
import asm.org.MusicStudio.services.RoomServiceImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class RoomBookingDialog extends Dialog<Room> {
    private ComboBox<Room> roomComboBox;
    private ComboBox<String> timeSlotComboBox;
    private final RoomService roomService;
    private LocalDate selectedDate;

    public RoomBookingDialog(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
        this.roomService = new RoomServiceImpl();
        
        setTitle("Book Room");
        setHeaderText("Book a Practice Room");
        
        // Create the grid for form layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Room selection
        roomComboBox = new ComboBox<>();
        roomComboBox.setPromptText("Select a room");
        roomComboBox.setConverter(new StringConverter<Room>() {
            @Override
            public String toString(Room room) {
                if (room == null) return "";
                return String.format("ID: %d - Room %s (Capacity: %d)", 
                    room.getId(),
                    room.getLocation(), 
                    room.getCapacity());
            }

            @Override
            public Room fromString(String string) {
                return null;
            }
        });

        // Time slot selection
        timeSlotComboBox = new ComboBox<>();
        timeSlotComboBox.setPromptText("Select time slot");
        timeSlotComboBox.getItems().addAll(
            "09:00-10:00",
            "10:00-11:00",
            "11:00-12:00",
            "13:00-14:00",
            "14:00-15:00",
            "15:00-16:00",
            "16:00-17:00",
            "17:00-18:00"
        );

        // Add form elements to grid
        grid.add(new Label("Room:"), 0, 0);
        grid.add(roomComboBox, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeSlotComboBox, 1, 1);

        // Load available rooms
        try {
            List<Room> rooms = roomService.getAvailableRooms(selectedDate);
            roomComboBox.setItems(FXCollections.observableArrayList(rooms));
        } catch (Exception e) {
            setHeaderText("Error loading rooms: " + e.getMessage());
        }

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Get the OK button
        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true); // Initially disable OK button

        // Add listeners to enable/disable OK button
        roomComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateInput(okButton));
        timeSlotComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateInput(okButton));

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Room selectedRoom = roomComboBox.getValue();
                if (selectedRoom != null) {
                    selectedRoom.setDate(selectedDate);
                    selectedRoom.setTimeSlot(timeSlotComboBox.getValue());
                    return selectedRoom;
                }
            }
            return null;
        });
    }

    private void validateInput(Button okButton) {
        boolean isValid = roomComboBox.getValue() != null && 
                         timeSlotComboBox.getValue() != null;
        okButton.setDisable(!isValid);
    }
} 