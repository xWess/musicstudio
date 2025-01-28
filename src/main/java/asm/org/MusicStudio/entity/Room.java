package asm.org.MusicStudio.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private Integer roomId;
    private String location;
    private Integer capacity;
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();
    private Artist bookedBy;
    
    public boolean isAvailable(LocalDate date, String time) {
        // Implementation for checking room availability
        return true; // placeholder
    }
}