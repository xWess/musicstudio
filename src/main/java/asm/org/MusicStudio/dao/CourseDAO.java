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
                String schedule = buildScheduleString(rs);
                    
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

    public List<Course> findAvailableCourses() throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name, 
                   (SELECT COUNT(*) FROM enrollments e 
                    WHERE e.course_id = c.id AND e.status = 'ACTIVE') as enrolled_count,
                   s.day_of_week, s.start_time, s.end_time, r.location,
                   c.monthly_fee
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            LEFT JOIN schedules s ON c.id = s.course_id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE u.active = true
        """;
        
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String schedule = buildScheduleString(rs);
                Course course = Course.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .monthlyFee(rs.getDouble("monthly_fee"))
                    .instructor(rs.getString("teacher_name"))
                    .maxStudents(rs.getInt("max_students"))
                    .schedule(schedule)
                    .build();
                
                courses.add(course);
            }
        }
        return courses;
    }

    public boolean hasAvailableSlots(Integer courseId) throws SQLException {
        String sql = """
            SELECT (c.max_students > (
                SELECT COUNT(*) FROM enrollments e 
                WHERE e.course_id = ? AND e.status = 'ACTIVE'
            )) as has_slots
            FROM courses c
            WHERE c.id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, courseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getBoolean("has_slots");
            }
        }
    }

    private String buildScheduleString(ResultSet rs) throws SQLException {
        Time startTime = rs.getTime("start_time");
        Time endTime = rs.getTime("end_time");
        String dayOfWeek = rs.getString("day_of_week");
        String location = rs.getString("location");
        
        if (startTime != null && endTime != null && dayOfWeek != null && location != null) {
            return String.format("%s %s-%s (%s)",
                dayOfWeek,
                startTime.toLocalTime().toString(),
                endTime.toLocalTime().toString(),
                location);
        }
        return "Schedule to be announced";
    }
    public Course findById(Integer id) throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name, 
                   s.day_of_week, s.start_time, s.end_time, r.location
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            LEFT JOIN schedules s ON c.id = s.course_id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE c.id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String schedule = buildScheduleString(rs);
                    return Course.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .monthlyFee(rs.getDouble("monthly_fee"))
                        .instructor(rs.getString("teacher_name"))
                        .maxStudents(rs.getInt("max_students"))
                        .schedule(schedule)
                        .build();
                }
                return null; // Return null if no course found
            }
        }
    }
} 