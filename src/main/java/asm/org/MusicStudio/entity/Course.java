package asm.org.MusicStudio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;
    
    @Column(nullable = false)
    private String name;
    
    @OneToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;
} 