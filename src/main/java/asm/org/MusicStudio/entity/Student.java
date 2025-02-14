package asm.org.MusicStudio.entity;
    
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends User {
    private List<Enrollment> enrollments;
    private List<Payment> payments;
    private LocalDate enrollmentDate;
    private String status;
    
    // JavaFX properties
    private final StringProperty statusProperty = new SimpleStringProperty();
    private final StringProperty enrollmentDateProperty = new SimpleStringProperty();
    
    public Student() {
        super();
        setRole(Role.STUDENT);
        this.enrollments = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public Student(Integer id, String name, String email) {
        this();
        setId(id);
        setName(name);
        setEmail(email);
    }

    // Factory method
    public static Student createStudent(Integer id, String name, String email) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setEmail(email);
        return student;
    }

    public StringProperty statusProperty() {
        return statusProperty;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.statusProperty.set(status);
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public void setEnrollmentDate(LocalDate date) {
        this.enrollmentDate = date;
        this.enrollmentDateProperty.set(date.toString());
    }
    
    public StringProperty enrollmentDateProperty() {
        return enrollmentDateProperty;
    }
} 