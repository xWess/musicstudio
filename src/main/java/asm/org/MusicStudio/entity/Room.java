package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private Integer roomId;
    private String location;
    private Integer capacity;
    private String type;
    private String availability;  // This will store the room status
    private String bookedByName;
    private String bookingTime;
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();
    @Builder.Default
    private BigDecimal price = BigDecimal.valueOf(50.00); // Default price if not set

    // JavaFX Properties
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty roomType = new SimpleStringProperty();
    private final IntegerProperty capacityProperty = new SimpleIntegerProperty();
    private final StringProperty availabilityProperty = new SimpleStringProperty();
    private final StringProperty equipment = new SimpleStringProperty();
    private final StringProperty bookedByNameProperty = new SimpleStringProperty();
    private final StringProperty bookingTimeProperty = new SimpleStringProperty();

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
        return availabilityProperty;
    }

    public StringProperty equipmentProperty() {
        return equipment;
    }

    public StringProperty bookedByNameProperty() {
        return bookedByNameProperty;
    }

    public StringProperty bookingTimeProperty() {
        return bookingTimeProperty;
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
        this.availability = value;
        this.availabilityProperty.set(value);
    }

    public void setEquipment(String value) {
        equipment.set(value);
    }

    public void setBookedByName(String name) {
        this.bookedByName = name;
        this.bookedByNameProperty.set(name);
    }

    public void setBookingTime(String time) {
        this.bookingTime = time;
        this.bookingTimeProperty.set(time);
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
        return this.availability;
    }

    public String getEquipment() {
        return equipment.get();
    }

    public String getBookingTime() {
        return this.bookingTime;
    }

    public boolean isAvailable(LocalDate date, String time) {
        return schedules.stream()
            .filter(s -> s.getDayOfWeek().equals(date.getDayOfWeek().toString()))
            .filter(s -> s.getStartTime().toString().equals(time))
            .allMatch(s -> s.getBookedBy() == null);
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }

    public void addEquipment(String name, String description) {
        // Implementation
    }

    public void addMaintenance(LocalDate date, String description) {
        // Implementation
    }

    public BigDecimal getPrice() {
        return price;
    }
}