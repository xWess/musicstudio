package asm.org.MusicStudio.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import asm.org.MusicStudio.entity.Schedule;

public class ScheduleServiceImpl {

    private Schedule mapToSchedule(ResultSet rs) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(rs.getInt("schedule_id"));
        schedule.setDate(rs.getDate("date").toLocalDate());
        schedule.setStartTime(rs.getTime("start_time").toLocalTime());
        schedule.setEndTime(rs.getTime("end_time").toLocalTime());
        schedule.setStatus(rs.getString("status"));
        
        // Load related objects (course, room, etc.)
        // Set them using the setter methods to update properties
        
        return schedule;
    }
} 