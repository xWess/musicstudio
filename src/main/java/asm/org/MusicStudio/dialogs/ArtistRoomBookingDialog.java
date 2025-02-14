package asm.org.MusicStudio.dialogs;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;
import asm.org.MusicStudio.services.ArtistRoomService;
import asm.org.MusicStudio.services.ArtistRoomServiceImpl;
import asm.org.MusicStudio.services.ArtistSessionService;
import asm.org.MusicStudio.services.ArtistSessionServiceImpl;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class ArtistRoomBookingDialog extends Dialog<Room> {
    private final ComboBox<LocalTime> startTimeCombo;
    private final Room room;
    private final ArtistSessionService artistSessionService;

    public ArtistRoomBookingDialog(Room room, LocalDate date) {
        this.room = room;
        this.artistSessionService = ArtistSessionServiceImpl.getInstance();
        
        // Verify artist is logged in
        if (!artistSessionService.isArtistLoggedIn()) {
            throw new IllegalStateException("You must be logged in as an artist to book rooms");
        }
        
        setTitle("Book Practice Room");
        setHeaderText("Select Booking Time");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        startTimeCombo = new ComboBox<>();
        startTimeCombo.setConverter(new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime time) {
                return time != null ? time.toString() : "";
            }

            @Override
            public LocalTime fromString(String string) {
                return string != null ? LocalTime.parse(string) : null;
            }
        });

        LocalTime time = LocalTime.of(9, 0);
        while (time.isBefore(LocalTime.of(21, 0))) {
            if (room.isAvailable(date, time.toString())) {
                startTimeCombo.getItems().add(time);
            }
            time = time.plusHours(1);
        }

        grid.add(new Label("Start Time:"), 0, 0);
        grid.add(startTimeCombo, 1, 0);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        startTimeCombo.valueProperty().addListener((obs, oldVal, newVal) -> 
            okButton.setDisable(newVal == null));

        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && startTimeCombo.getValue() != null) {
                Schedule schedule = Schedule.builder()
                    .date(date)
                    .startTime(startTimeCombo.getValue())
                    .endTime(startTimeCombo.getValue().plusHours(1))
                    .room(room)
                    .status("BOOKED")
                    .build();
                room.addSchedule(schedule);
                return room;
            }
            return null;
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 