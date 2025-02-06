package asm.org.MusicStudio.entity;

import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private Integer id;
    private User student;
    private Course course;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}