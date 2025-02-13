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
}