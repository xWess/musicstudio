package asm.org.MusicStudio.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
    private List<Course> assignedCourses;

    public Teacher() {
        super();
        setRole(Role.TEACHER);
        this.assignedCourses = new ArrayList<>();
    }

    public Teacher(Integer id, String name, String email) {
        this();
        setId(id);
        setName(name);
        setEmail(email);
    }

    // Factory method
    public static Teacher createTeacher(Integer id, String name, String email) {
        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setName(name);
        teacher.setEmail(email);
        return teacher;
    }
} 