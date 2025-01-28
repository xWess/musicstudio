package asm.org.MusicStudio.entity;

public class Enrollment {
    private Long id;
    private Student student;
    private Course course;

    // Default constructor
    public Enrollment() {}

    // Parameterized constructor
    public Enrollment(Long id, Student student, Course course) {
        this.id = id;
        this.student = student;
        this.course = course;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
} 