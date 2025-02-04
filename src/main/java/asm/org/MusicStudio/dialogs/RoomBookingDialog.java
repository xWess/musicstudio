package asm.org.MusicStudio.dialogs;

import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.LocalTime;

public class RoomBookingDialog extends Dialog<Room> {
    private TextField locationField;
    private Spinner<Integer> capacitySpinner;
    private DatePicker datePicker;
    private ComboBox<LocalTime> timeSlotComboBox;
    private TextArea equipmentArea;

    public RoomBookingDialog(LocalDate selectedDate) {
        setTitle("Book a Room");
        setHeaderText("Please enter room booking details");

        // Create form controls
        setupControls(selectedDate);

        // Create layout
        GridPane grid = createLayout();
        getDialogPane().setContent(grid);

        // Add buttons
        ButtonType bookButton = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(bookButton, ButtonType.CANCEL);

        // Enable/Disable book button based on form validation
        Node bookButtonNode = getDialogPane().lookupButton(bookButton);
        bookButtonNode.setDisable(true);

        // Add validation listeners
        setupValidation(bookButtonNode);

        // Convert the result
        setResultConverter(dialogButton -> {
            if (dialogButton == bookButton) {
                Room room = Room.builder()
                    .location(locationField.getText())
                    .capacity(capacitySpinner.getValue())
                    .build();

                Schedule schedule = new Schedule();
                schedule.setDate(datePicker.getValue());
                schedule.setTime(timeSlotComboBox.getValue().toString());
                room.addSchedule(schedule);

                return room;
            }
            return null;
        });
    }

    private void setupControls(LocalDate selectedDate) {
        locationField = new TextField();
        
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1);
        capacitySpinner = new Spinner<>();
        capacitySpinner.setValueFactory(valueFactory);
        capacitySpinner.setEditable(true);

        datePicker = new DatePicker(selectedDate);
        datePicker.setEditable(false);
        // Prevent selecting dates before today
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        timeSlotComboBox = new ComboBox<>();
        LocalTime startTime = LocalTime.of(9, 0); // 9 AM
        LocalTime endTime = LocalTime.of(21, 0);  // 9 PM
        while (startTime.isBefore(endTime)) {
            timeSlotComboBox.getItems().add(startTime);
            startTime = startTime.plusHours(1);
        }

        timeSlotComboBox.setConverter(new StringConverter<LocalTime>() {
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

        // Equipment TextArea
        equipmentArea = new TextArea();
        equipmentArea.setPrefRowCount(3);
        equipmentArea.setWrapText(true);
    }

    private GridPane createLayout() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Location:"), 0, 0);
        grid.add(locationField, 1, 0);
        grid.add(new Label("Capacity:"), 0, 1);
        grid.add(capacitySpinner, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Time Slot:"), 0, 3);
        grid.add(timeSlotComboBox, 1, 3);
        grid.add(new Label("Equipment Needed:"), 0, 4);
        grid.add(equipmentArea, 1, 4);

        return grid;
    }

    private void setupValidation(Node bookButton) {
        // Add listeners to enable/disable the book button
        locationField.textProperty().addListener((obs, oldVal, newVal) -> 
            validateForm(bookButton));
        capacitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateForm(bookButton));
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateForm(bookButton));
        timeSlotComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateForm(bookButton));
    }

    private void validateForm(Node bookButton) {
        boolean isValid = !locationField.getText().isEmpty() &&
                         capacitySpinner.getValue() != null &&
                         capacitySpinner.getValue() > 0 &&
                         datePicker.getValue() != null &&
                         !datePicker.getValue().isBefore(LocalDate.now()) &&
                         timeSlotComboBox.getValue() != null;
        
        bookButton.setDisable(!isValid);
    }
} 