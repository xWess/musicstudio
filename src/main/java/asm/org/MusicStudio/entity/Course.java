package asm.org.MusicStudio.entity;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private Integer courseId;
    private String name;
    private Schedule schedule;
    private Teacher teacher;
    private List<Enrollment> enrollments = new ArrayList<>();

    // Default constructor
    public Course() {}

    // Parameterized constructor
    public Course(Integer courseId, String name, Schedule schedule, Teacher teacher) {
        this.courseId = courseId;
        this.name = name;
        this.schedule = schedule;
        this.teacher = teacher;
    }

    // Getters and setters
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }

    // Helper methods
    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
    }
} 