package asm.org.MusicStudio.dialogs;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.services.RoomService;
import asm.org.MusicStudio.services.RoomServiceImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class RoomBookingDialog extends Dialog<Room> {
    private final LocalDate bookingDate;
    private final RoomService roomService;
    private ComboBox<Room> roomCombo;
    private ComboBox<LocalTime> startTimeCombo;
    private TextField locationField;
    private Spinner<Integer> capacitySpinner;
    private DatePicker datePicker;
    private TextArea equipmentArea;

    public RoomBookingDialog(LocalDate date) {
        this.bookingDate = date;
        this.roomService = RoomServiceImpl.getInstance();
        
        setTitle("Book Practice Room");
        setHeaderText("Select Room and Time");
        
        getDialogPane().setContent(createContent());
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return createBooking();
            }
            return null;
        });

        validateForm();
        roomCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        startTimeCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private Node createContent() {
        try {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            roomCombo = new ComboBox<>();
            roomCombo.setItems(FXCollections.observableArrayList(roomService.getAllRooms()));
            grid.add(new Label("Room:"), 0, 0);
            grid.add(roomCombo, 1, 0);

            startTimeCombo = new ComboBox<>();
            LocalTime startTime = LocalTime.of(9, 0); // 9 AM
            LocalTime endTime = LocalTime.of(21, 0);  // 9 PM
            while (startTime.isBefore(endTime)) {
                startTimeCombo.getItems().add(startTime);
                startTime = startTime.plusHours(1);
            }
            startTimeCombo.setConverter(new StringConverter<LocalTime>() {
                @Override
                public String toString(LocalTime time) {
                    if (time == null) return "";
                    return time.toString();
                }

                @Override
                public LocalTime fromString(String string) {
                    if (string == null || string.isEmpty()) return null;
                    return LocalTime.parse(string);
                }
            });
            grid.add(new Label("Time:"), 0, 1);
            grid.add(startTimeCombo, 1, 1);

            locationField = new TextField();
            grid.add(new Label("Location:"), 0, 2);
            grid.add(locationField, 1, 2);

            SpinnerValueFactory<Integer> valueFactory = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
            capacitySpinner = new Spinner<>();
            capacitySpinner.setValueFactory(valueFactory);
            capacitySpinner.setEditable(true);
            grid.add(new Label("Capacity:"), 0, 3);
            grid.add(capacitySpinner, 1, 3);

            datePicker = new DatePicker(bookingDate);
            datePicker.setEditable(false);
            // Prevent selecting dates before today
            datePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(LocalDate.now()));
                }
            });
            grid.add(new Label("Date:"), 0, 4);
            grid.add(datePicker, 1, 4);

            equipmentArea = new TextArea();
            equipmentArea.setPrefRowCount(3);
            equipmentArea.setWrapText(true);
            grid.add(new Label("Equipment Needed:"), 0, 5);
            grid.add(equipmentArea, 1, 5);

            return grid;
        } catch (SQLException e) {
            showError("Error loading rooms: " + e.getMessage());
            return new Label("Error loading rooms");
        }
    }

    private Room createBooking() {
        Room selectedRoom = roomCombo.getValue();
        if (selectedRoom != null && startTimeCombo.getValue() != null) {
            Schedule schedule = Schedule.builder()
                .date(datePicker.getValue())
                .startTime(startTimeCombo.getValue())
                .endTime(startTimeCombo.getValue().plusHours(1))
                .room(selectedRoom)
                .status("PENDING")
                .build();
            
            selectedRoom.addSchedule(schedule);
            return selectedRoom;
        }
        return null;
    }

    private void validateForm() {
        boolean isValid = roomCombo.getValue() != null &&
                         startTimeCombo.getValue() != null;
        
        Node okButton = getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(!isValid);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 