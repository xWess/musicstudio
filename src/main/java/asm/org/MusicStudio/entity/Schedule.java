package asm.org.MusicStudio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private Integer scheduleId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Course course;
    private Room room;
} 