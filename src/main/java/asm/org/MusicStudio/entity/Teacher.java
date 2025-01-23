package asm.org.MusicStudio.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher extends User {
    @Column(name = "teacher_id", unique = true)
    private Integer teacherId;
    
    @OneToMany(mappedBy = "teacher")
    private List<Course> assignedCourses;
} 