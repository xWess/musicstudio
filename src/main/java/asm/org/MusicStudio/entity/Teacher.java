package asm.org.MusicStudio.entity;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {
    private List<Course> assignedCourses = new ArrayList<>();

    public Teacher() {
        super();
    }

    public Teacher(Integer id, String name, String email, String role) {
        super(id, name, email, Role.valueOf(role.toUpperCase()));
        this.assignedCourses = new ArrayList<>();
    }
} 