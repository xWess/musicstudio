package asm.org.MusicStudio.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import asm.org.MusicStudio.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class ScheduleServiceImpl {

    private Schedule mapToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        
        LocalDate date = rs.getDate("date").toLocalDate();
        schedule.setDate(date);
        
        // Convertir LocalTime en LocalDateTime
        LocalTime startTime = rs.getTime("start_time").toLocalTime();
        LocalTime endTime = rs.getTime("end_time").toLocalTime();
        
        schedule.setTimeRange(
            LocalDateTime.of(date, startTime),
            LocalDateTime.of(date, endTime)
        );
        
        schedule.setStatus(rs.getString("status"));
        
        // Load related objects (course, room, etc.)
        // Set them using the setter methods to update properties
        
        return schedule;
    }
} 