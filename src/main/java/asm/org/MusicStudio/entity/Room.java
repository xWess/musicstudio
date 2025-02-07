package asm.org.MusicStudio.entity;

import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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