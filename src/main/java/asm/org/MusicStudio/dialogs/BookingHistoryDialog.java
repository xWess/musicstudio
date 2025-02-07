package asm.org.MusicStudio.dialogs;

import java.util.List;
import asm.org.MusicStudio.entity.Room;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class BookingHistoryDialog extends Dialog<Void> {
    
    public BookingHistoryDialog(List<Room> bookings) {
        setTitle("Booking History");
        setHeaderText("Your Room Booking History");
        
        TableView<Room> table = new TableView<>();
        
        TableColumn<Room, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(data -> data.getValue().roomNumberProperty());
        
        TableColumn<Room, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDate().toString()));
        
        TableColumn<Room, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getTimeSlot()));
        
        table.getColumns().addAll(roomCol, dateCol, timeCol);
        table.setItems(FXCollections.observableArrayList(bookings));
        
        VBox content = new VBox(10);
        content.getChildren().add(table);
        
        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }
} 