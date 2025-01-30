package asm.org.MusicStudio.entity;
    
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Student extends User {
    private Set<Course> courses = new HashSet<>();
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student() {
        super();
    }

    public Student(Integer id, String name, String email, String role) {
        super(id, name, email, Role.valueOf(role.toUpperCase()));
        this.courses = new HashSet<>();
        this.enrollments = new ArrayList<>();
    }
} 