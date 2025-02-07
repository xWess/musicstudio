package asm.org.MusicStudio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Integer scheduleId;
    private LocalDate date;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Course course;
    private Room room;
    private Artist bookedBy; // This field can be null for course schedules
    private String status; // e.g., "BOOKED", "AVAILABLE", "CANCELLED"

    // JavaFX Properties
    private final StringProperty timeProperty = new SimpleStringProperty();
    private final StringProperty courseProperty = new SimpleStringProperty();
    private final StringProperty teacherProperty = new SimpleStringProperty();
    private final StringProperty roomProperty = new SimpleStringProperty();
    private final StringProperty statusProperty = new SimpleStringProperty();

    // Property getters
    public StringProperty timeProperty() {
        timeProperty.set(getTime());
        return timeProperty;
    }

    public StringProperty courseProperty() {
        courseProperty.set(course != null ? course.getName() : "");
        return courseProperty;
    }

    public StringProperty teacherProperty() {
        teacherProperty.set(course != null && course.getTeacher() != null ? 
            course.getTeacher().getName() : "");
        return teacherProperty;
    }

    public StringProperty roomProperty() {
        roomProperty.set(room != null ? room.getRoomNumber() : "");
        return roomProperty;
    }

    public StringProperty statusProperty() {
        statusProperty.set(status);
        return statusProperty;
    }

    // Update properties when fields change
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        timeProperty.set(getTime());
    }

    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            courseProperty.set(course.getName());
            teacherProperty.set(course.getTeacher() != null ? 
                course.getTeacher().getName() : "");
        }
    }

    public void setRoom(Room room) {
        this.room = room;
        if (room != null) {
            roomProperty.set(room.getRoomNumber());
        }
    }

    public void setStatus(String status) {
        this.status = status;
        statusProperty.set(status);
    }

    // Time-related methods
    public void setTime(String timeString) {
        if (timeString != null && !timeString.isEmpty()) {
            try {
                this.startTime = LocalTime.parse(timeString);
                // Assuming 1-hour duration by default, can be modified as needed
                this.endTime = this.startTime.plusHours(1);
                timeProperty.set(getTime());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid time format: " + timeString);
            }
        }
    }

    // Helper method to set both start and end times
    public void setTimeRange(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        timeProperty.set(getTime());
    }

    // Get formatted time range string
    public String getTime() {
        if (startTime == null) return "";
        return startTime.toString() + " - " + 
               (endTime != null ? endTime.toString() : startTime.plusHours(1).toString());
    }

    // Convenience method to get date
    public LocalDate getDate() {
        return date;
    }

    // Convenience method to set date
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Helper method to check if schedule overlaps with given time range
    public boolean overlaps(LocalTime otherStart, LocalTime otherEnd) {
        return !startTime.isAfter(otherEnd) && !endTime.isBefore(otherStart);
    }

    // Helper method to check if schedule is for given date
    public boolean isForDate(LocalDate otherDate) {
        return date != null && date.equals(otherDate);
    }

    public boolean isActive(LocalDate date, LocalTime time) {
        return this.date != null && date != null &&
               this.date.equals(date) && 
               !time.isBefore(startTime) && 
               !time.isAfter(endTime);
    }
}