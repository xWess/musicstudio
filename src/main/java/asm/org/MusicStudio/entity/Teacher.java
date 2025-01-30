package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User {
    @Builder.Default
    private List<Course> assignedCourses = new ArrayList<>();

    public Teacher(Integer id, String name, String email, String role) {
        super(id, name, email, Role.valueOf(role.toUpperCase()));
        this.assignedCourses = new ArrayList<>();
    }
} 