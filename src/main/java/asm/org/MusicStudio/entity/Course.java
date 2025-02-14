package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Integer id;
    private String name;
    private String description;
    private Integer teacherId;
    private String teacherName;
    private String schedule;
    private Double monthlyFee;
    private Integer maxStudents;
    private List<Schedule> schedules;

    // JavaFX Properties
    private final StringProperty nameProperty = new SimpleStringProperty();
    private final StringProperty descriptionProperty = new SimpleStringProperty();
    private final StringProperty teacherNameProperty = new SimpleStringProperty();
    private final IntegerProperty teacherIdProperty = new SimpleIntegerProperty();
    private final DoubleProperty monthlyFeeProperty = new SimpleDoubleProperty();
    private final IntegerProperty maxStudentsProperty = new SimpleIntegerProperty();
    
    public StringProperty nameProperty() {
        return nameProperty;
    }
    
    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }
    
    public StringProperty teacherNameProperty() {
        return teacherNameProperty;
    }
    
    public IntegerProperty teacherIdProperty() {
        return teacherIdProperty;
    }
    
    public DoubleProperty monthlyFeeProperty() {
        return monthlyFeeProperty;
    }
    
    public IntegerProperty maxStudentsProperty() {
        return maxStudentsProperty;
    }
    
    // Update setters to update both fields and properties
    public void setName(String name) {
        this.name = name;
        this.nameProperty.set(name);
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.descriptionProperty.set(description);
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
        this.teacherNameProperty.set(teacherName);
    }
    
    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
        this.teacherIdProperty.set(teacherId);
    }
    
    public void setMonthlyFee(Double monthlyFee) {
        this.monthlyFee = monthlyFee;
        this.monthlyFeeProperty.set(monthlyFee != null ? monthlyFee : 0.0);
    }
    
    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
        this.maxStudentsProperty.set(maxStudents != null ? maxStudents : 0);
    }

    public List<Schedule> getSchedules() {
        return schedules != null ? schedules : new ArrayList<>();
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules != null ? new ArrayList<>(schedules) : new ArrayList<>();
    }

    // Add to CourseBuilder
    public static class CourseBuilder {
        private List<Schedule> schedules = new ArrayList<>();
        
        public CourseBuilder instructor(String instructor) {
            this.teacherName = instructor;
            return this;
        }
        
        public CourseBuilder description(String description) {
            this.description = description;
            return this;
        }
    }

    // Add these methods
    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getInstructor() {
        return teacherName;  // Map instructor to teacherName
    }

    public void setInstructor(String instructor) {
        this.teacherName = instructor;
        this.teacherNameProperty.set(instructor);
    }
}