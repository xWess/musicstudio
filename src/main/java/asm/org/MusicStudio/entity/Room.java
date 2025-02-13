package asm.org.MusicStudio.entity;
package asm.org.MusicStudio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private Integer roomId;
    private String location;
    private Integer capacity;
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();

    // JavaFX Properties
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty roomType = new SimpleStringProperty();
    private final IntegerProperty capacityProperty = new SimpleIntegerProperty();
    private final StringProperty availability = new SimpleStringProperty();
    private final StringProperty equipment = new SimpleStringProperty();


public class Room {
    private int id;
    private final StringProperty location = new SimpleStringProperty();
    private final IntegerProperty capacity = new SimpleIntegerProperty();
    private final StringProperty availability = new SimpleStringProperty("Available");
    private LocalDate date;
    private String timeSlot;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLocation() { return location.get(); }
    public void setLocation(String location) { this.location.set(location); }
    public StringProperty locationProperty() { return location; }
    
    public int getCapacity() { return capacity.get(); }
    public void setCapacity(int capacity) { this.capacity.set(capacity); }
    public IntegerProperty capacityProperty() { return capacity; }
    
    public String getAvailability() { return availability.get(); }
    public void updateAvailabilityProperty(boolean isBooked) {
        availability.set(isBooked ? "Booked" : "Available");
    }
    public StringProperty availabilityProperty() { return availability; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    // For TableView compatibility
    public String getRoomNumber() { return location.get(); }
    public StringProperty roomNumberProperty() {
        StringProperty roomNumber = new SimpleStringProperty();
        roomNumber.set(String.format("Room %s", location.get()));
        return roomNumber;
    }
    public StringProperty roomTypeProperty() {
        return roomType;
    }

    public IntegerProperty capacityProperty() {
        return capacityProperty;
    }

    public StringProperty availabilityProperty() {
        return availability;
    }

    public StringProperty equipmentProperty() {
        return equipment;
    }

    // Property setters
    public void setRoomNumber(String value) {
        roomNumber.set(value);
    }

    public void setRoomType(String value) {
        roomType.set(value);
    }

    public void setCapacityProperty(int value) {
        capacityProperty.set(value);
    }

    public void setAvailability(String value) {
        availability.set(value);
    }

    public void setEquipment(String value) {
        equipment.set(value);
    }

    // Property value getters
    public String getRoomNumber() {
        return roomNumber.get();
    }

    public String getRoomType() {
        return roomType.get();
    }

    public int getCapacityPropertyValue() {
        return capacityProperty.get();
    }

    public String getAvailability() {
        return availability.get();
    }

    public String getEquipment() {
        return equipment.get();
    }

    public boolean isAvailable(LocalDate date, String time) {
        // Implementation for checking room availability
        return true; // placeholder
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);

    // Add these methods if not already present
    public String getDisplayInfo() {
        if (timeSlot != null && date != null) {
            return String.format("%s (Booked for %s at %s)", 
                location.get(), 
                date.toString(), 
                timeSlot);
        }
        return location.get();
    }

    public StringProperty displayInfoProperty() {
        StringProperty display = new SimpleStringProperty();
        display.set(getDisplayInfo());
        return display;
    }
}