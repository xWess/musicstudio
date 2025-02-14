package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CourseFile {
    private Integer id;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadDate;
    private Integer courseId;
    private Integer teacherId;
    private String fileType;
    private String description;
    private Long fileSize;
    
    // Transient fields for UI display
    private String courseName;
    private String teacherName;
    
    // Add these fields
    @Builder.Default
    private final StringProperty fileNameProperty = new SimpleStringProperty();
    @Builder.Default
    private final StringProperty courseNameProperty = new SimpleStringProperty();
    @Builder.Default
    private final StringProperty teacherNameProperty = new SimpleStringProperty();
    @Builder.Default
    private final StringProperty descriptionProperty = new SimpleStringProperty();
    
    // Add these fields
    private Course course;
    private User teacher;
    
    // Add getters for properties
    public StringProperty fileNameProperty() {
        return fileNameProperty;
    }

    public StringProperty courseNameProperty() {
        return courseNameProperty;
    }

    public StringProperty teacherNameProperty() {
        return teacherNameProperty;
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }
    
    // Update setters to set both field and property
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.fileNameProperty.set(fileName);
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
        this.courseNameProperty.set(courseName);
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
        this.teacherNameProperty.set(teacherName);
    }

    public void setDescription(String description) {
        this.description = description;
        this.descriptionProperty.set(description);
    }
    
    public String getFormattedSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    // Add these methods
    public void setCourse(Course course) {
        this.course = course;
        this.courseId = course.getId();
        this.courseName = course.getName();
        this.courseNameProperty.set(course.getName());
    }

    public Course getCourse() {
        return course;
    }
} 