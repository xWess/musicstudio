package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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
    private User instructor;
    private Double monthlyFee;
    private Integer maxStudents;
    private Integer enrolledCount;
    private String schedule;
    private String status;
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();
    private int instructorId;
    private boolean active;

    // Helper methods
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }

    public Integer getInstructorId() {
        return instructor != null ? instructor.getId() : null;
    }

    public String getInstructorName() {
        return instructor != null ? instructor.getName() : "";
    }

    public String getInstructorDisplay() {
        return instructor != null ? instructor.getName() : "";
    }

    public User getInstructor() {
        return instructor;
    }

    // Helper method to safely get instructor name
    public String getInstructorNameSafe() {
        return getInstructor() != null ? getInstructor().getName() : "";
    }

    @Override
    public String toString() {
        return instructor != null ? instructor.getName() : "";
    }
}