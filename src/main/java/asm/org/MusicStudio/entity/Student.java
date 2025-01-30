package asm.org.MusicStudio.entity;
    
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Student extends User {
    @Builder.Default
    private Set<Course> courses = new HashSet<>();
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();

    public Student(Integer id, String name, String email, String role) {
        super(id, name, email, Role.valueOf(role.toUpperCase()));
        this.courses = new HashSet<>();
        this.enrollments = new ArrayList<>();
    }
} 