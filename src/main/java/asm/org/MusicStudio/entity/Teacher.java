package asm.org.MusicStudio.entity;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {
    private List<Course> assignedCourses = new ArrayList<>();

    public Teacher() {
        super();
        setRole(Role.TEACHER);
    }

    public Teacher(Integer id, String name, String email) {
        super();
        setId(id);
        setName(name);
        setEmail(email);
        setRole(Role.TEACHER);
        this.assignedCourses = new ArrayList<>();
    }
} 