package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import javafx.beans.property.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private Integer id;
    private User student;
    private Course course;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE, COMPLETED, CANCELLED
    private Payment payment;
    
    // JavaFX Properties
    private final StringProperty courseNameProperty = new SimpleStringProperty();
    private final StringProperty instructorProperty = new SimpleStringProperty();
    private final StringProperty scheduleProperty = new SimpleStringProperty();
    private final StringProperty statusProperty = new SimpleStringProperty();
    
    // Property getters
    public StringProperty courseNameProperty() {
        courseNameProperty.set(course != null ? course.getName() : "");
        return courseNameProperty;
    }
    
    public StringProperty instructorProperty() {
        instructorProperty.set(course != null ? course.getInstructor() : "");
        return instructorProperty;
    }
    
    public StringProperty scheduleProperty() {
        scheduleProperty.set(course != null ? course.getSchedule() : "");
        return scheduleProperty;
    }
    
    public StringProperty statusProperty() {
        statusProperty.set(status);
        return statusProperty;
    }
    
    // Update properties when course or status changes
    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            courseNameProperty.set(course.getName());
            instructorProperty.set(course.getInstructor());
            scheduleProperty.set(course.getSchedule());
        }
    }
    
    public void setStatus(String status) {
        this.status = status;
        statusProperty.set(status);
    }
}