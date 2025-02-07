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
    private User teacher;
    private String schedule;
    private String description;
    private Double monthlyFee;
    private Integer maxStudents;
    private Integer enrolledCount;
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    // Helper methods
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }

    public Integer getTeacherId() {
        return teacher != null ? teacher.getId() : null;
    }

    public String getTeacherName() {
        return teacher != null ? teacher.getName() : null;
    }

    @Override
    public String toString() {
        return name;
    }
}