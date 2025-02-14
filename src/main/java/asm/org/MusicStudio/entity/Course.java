package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Integer id;
    private String name;
    private String description;
    private Double monthlyFee;
    private String instructor;
    private Integer maxStudents;
    private String schedule; // e.g., "Monday, Wednesday 15:00-16:30"
    private String room;  // Add this field
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    // Helper methods
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }

    @Override
    public String toString() {
        return "Course{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", monthlyFee=" + monthlyFee +
            ", instructor='" + instructor + '\'' +
            ", maxStudents=" + maxStudents +
            ", room='" + room + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) &&
               Objects.equals(name, course.name) &&
               Objects.equals(description, course.description) &&
               Objects.equals(monthlyFee, course.monthlyFee) &&
               Objects.equals(instructor, course.instructor) &&
               Objects.equals(maxStudents, course.maxStudents) &&
               Objects.equals(room, course.room);
    }
}