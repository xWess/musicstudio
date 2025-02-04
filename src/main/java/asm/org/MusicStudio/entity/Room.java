package asm.org.MusicStudio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    // Property getters
    public StringProperty roomNumberProperty() {
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
    }

    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }
}