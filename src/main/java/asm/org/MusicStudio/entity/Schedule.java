package asm.org.MusicStudio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Integer id;
    private Integer courseId;
    private String courseName;
    private Integer teacherId;
    private String teacherName;
    private Integer roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDate date;
    private String dayOfWeek;
    private Room room;
    private Course course;
    private Integer scheduleId;
    private User bookedBy;
    private String bookedByName;

    // JavaFX properties
    private final StringProperty courseProperty = new SimpleStringProperty();
    private final StringProperty teacherProperty = new SimpleStringProperty();
    private final StringProperty roomProperty = new SimpleStringProperty();
    private final StringProperty statusProperty = new SimpleStringProperty();
    private final StringProperty bookedByProperty = new SimpleStringProperty();

    // Méthode utilitaire pour convertir LocalTime en LocalDateTime
    public static LocalDateTime toDateTime(LocalDate date, LocalTime time) {
        return date != null && time != null ? LocalDateTime.of(date, time) : null;
    }

    // Méthode pour le builder qui accepte LocalTime
    public static class ScheduleBuilder {
        public ScheduleBuilder startTime(LocalTime time) {
            this.startTime = toDateTime(this.date, time);
            return this;
        }

        public ScheduleBuilder endTime(LocalTime time) {
            this.endTime = toDateTime(this.date, time);
            return this;
        }
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
        this.courseProperty.set(courseName);
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
        this.teacherProperty.set(teacherName);
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
        this.roomProperty.set(roomName);
    }

    public void setStatus(String status) {
        this.status = status;
        this.statusProperty.set(status);
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        if (room != null) {
            this.roomId = room.getRoomId();
            this.roomName = room.getLocation();
            this.roomProperty.set(room.getLocation());
        }
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            this.courseId = course.getId();
            this.courseName = course.getName();
            this.courseProperty.set(course.getName());
        }
    }

    public Integer getScheduleId() {
        return id != null ? id : scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
        this.id = scheduleId;
    }

    public void setBookedBy(User user) {
        this.bookedBy = user;
        if (user != null) {
            this.bookedByName = user.getName();
            this.bookedByProperty.set(user.getName());
        } else {
            this.bookedByName = null;
            this.bookedByProperty.set(null);
        }
    }

    public User getBookedBy() {
        return bookedBy;
    }

    // Property getters for JavaFX
    public StringProperty courseProperty() {
        return courseProperty;
    }

    public StringProperty teacherProperty() {
        return teacherProperty;
    }

    public StringProperty roomProperty() {
        return roomProperty;
    }

    public StringProperty statusProperty() {
        return statusProperty;
    }

    public StringProperty bookedByProperty() {
        return bookedByProperty;
    }

    // Time-related methods
    public void setTime(String timeString) {
        if (timeString != null && !timeString.isEmpty()) {
            try {
                this.startTime = LocalDateTime.parse(timeString);
                // Assuming 1-hour duration by default, can be modified as needed
                this.endTime = this.startTime.plusHours(1);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid time format: " + timeString);
            }
        }
    }

    // Helper method to set both start and end times
    public void setTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
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
    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return !startTime.isAfter(otherEnd) && !endTime.isBefore(otherStart);
    }

    // Helper method to check if schedule is for given date
    public boolean isForDate(LocalDate otherDate) {
        return date != null && date.equals(otherDate);
    }
  
  public StringProperty timeProperty() {
        SimpleStringProperty timeProperty = new SimpleStringProperty();
        if (startTime != null) {
            String timeString = startTime.toLocalTime() + " - " + 
                (endTime != null ? endTime.toLocalTime() : startTime.plusHours(1).toLocalTime());
            timeProperty.set(timeString);
        }
        return timeProperty;
  @Override
    public String toString() {
        return "Schedule{" +
            "scheduleId=" + scheduleId +
            ", date=" + date +
            ", dayOfWeek='" + dayOfWeek + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", courseName=" + (course != null ? course.getName() : "null") +
            ", roomLocation=" + (room != null ? room.getLocation() : "null") +
            ", status='" + status + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(scheduleId, schedule.scheduleId) &&
               Objects.equals(date, schedule.date) &&
               Objects.equals(dayOfWeek, schedule.dayOfWeek) &&
               Objects.equals(startTime, schedule.startTime) &&
               Objects.equals(endTime, schedule.endTime) &&
               Objects.equals(status, schedule.status) &&
               Objects.equals(course != null ? course.getId() : null, 
                            schedule.course != null ? schedule.course.getId() : null) &&
               Objects.equals(room != null ? room.getRoomNumber() : null,
                            schedule.room != null ? schedule.room.getRoomNumber() : null);
    }
}