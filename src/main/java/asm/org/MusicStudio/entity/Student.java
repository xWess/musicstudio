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
@AllArgsConstructor
@SuperBuilder
public class Student extends User {
    private Integer studentId;
    private String name;
    private String email;
    @Builder.Default
    private Set<Course> courses = new HashSet<>();
    @Builder.Default
    private List<Enrollment> enrollments = new ArrayList<>();
} 