package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    
    public List<Course> findAllActiveCourses() throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name, 
                   s.day_of_week, s.start_time, s.end_time, r.location
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            LEFT JOIN schedules s ON c.id = s.course_id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE u.active = true
            ORDER BY c.name""";
            
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String schedule;
                Time startTime = rs.getTime("start_time");
                Time endTime = rs.getTime("end_time");
                String dayOfWeek = rs.getString("day_of_week");
                String location = rs.getString("location");
                
                if (startTime != null && endTime != null && dayOfWeek != null && location != null) {
                    schedule = String.format("%s %s-%s (%s)",
                        dayOfWeek,
                        startTime.toLocalTime().toString(),
                        endTime.toLocalTime().toString(),
                        location);
                } else {
                    schedule = "Schedule to be announced";
                }
                    
                Course course = Course.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("name") + " with " + rs.getString("teacher_name"))
                    .monthlyFee(80.00) // Default fee since it's not in DB yet
                    .instructor(rs.getString("teacher_name"))
                    .maxStudents(20) // Default capacity
                    .schedule(schedule)
                    .build();
                    
                courses.add(course);
            }
        }
        return courses;
    }
} 