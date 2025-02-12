package asm.org.MusicStudio.models;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import asm.org.MusicStudio.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Integer id;
    private String name;
    private String description;
    private User teacher;
    private Double monthlyFee;
    private Integer maxStudents;
    private Integer enrolledCount;
    private String schedule;
    private String status;
} 