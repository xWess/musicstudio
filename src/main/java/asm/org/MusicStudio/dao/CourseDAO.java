package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.User;
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

    public List<Course> findCoursesByTeacherId(int teacherId) throws SQLException {
        String sql = """
            SELECT c.*, COUNT(e.id) as enrolled_count
            FROM courses c
            LEFT JOIN enrollments e ON c.id = e.course_id
            WHERE c.teacher_id = ?
            GROUP BY c.id
            ORDER BY c.name""";
            
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = Course.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .teacherId(teacherId)
                        .enrolledCount(rs.getInt("enrolled_count"))
                        .build();
                    courses.add(course);
                }
            }
        }
        return courses;
    }

    public List<Course> findByTeacherId(int teacherId) throws SQLException {
        System.out.println("CourseDAO: Executing findByTeacherId for teacher " + teacherId);
        List<Course> courses = new ArrayList<>();
        String sql = """
            SELECT c.*, u.name as teacher_name, u.email as teacher_email,
                   s.id as schedule_id, s.day_of_week, s.start_time, s.end_time,
                   r.id as room_id, r.location,
                   u2.name as booked_by_name,
                   (SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.id) as enrolled_count
            FROM courses c
            LEFT JOIN users u ON c.teacher_id = u.id
            LEFT JOIN schedules s ON c.id = s.course_id
            LEFT JOIN rooms r ON s.room_id = r.id
            LEFT JOIN users u2 ON s.booked_by = u2.id
            WHERE c.teacher_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            System.out.println("CourseDAO: Executing SQL with teacherId = " + teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String schedule = formatSchedule(rs);
                System.out.println("CourseDAO: Found course with schedule: " + schedule);
                
                User teacher = new User();  // Create User object using no-args constructor
                teacher.setId(teacherId);
                teacher.setName(rs.getString("teacher_name"));
                teacher.setEmail(rs.getString("teacher_email"));
                
                Course course = Course.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .teacher(teacher)  // Use the properly constructed User object
                    .schedule(schedule)
                    .teacherId(teacherId)
                    .description(rs.getString("description"))
                    .monthlyFee(rs.getDouble("monthly_fee"))
                    .maxStudents(rs.getInt("max_students"))
                    .enrolledCount(rs.getInt("enrolled_count"))
                    .build();
                    
                courses.add(course);
            }
        }
        System.out.println("CourseDAO: Returning " + courses.size() + " courses");
        return courses;
    }

    private String formatSchedule(ResultSet rs) throws SQLException {
        try {
            Time startTime = rs.getTime("start_time");
            Time endTime = rs.getTime("end_time");
            String dayOfWeek = rs.getString("day_of_week");
            
            if (startTime == null || endTime == null || dayOfWeek == null) {
                return "Schedule to be announced";
            }
            
            String scheduleStr = String.format("%s %s-%s", 
                dayOfWeek,
                startTime.toLocalTime().toString(),
                endTime.toLocalTime().toString());
                
            String location = rs.getString("location");
            if (location != null) {
                scheduleStr += String.format(" (Room: %s)", location);
            }
            
            String bookedBy = rs.getString("booked_by_name");
            if (bookedBy != null) {
                scheduleStr += String.format(" [Booked by: %s]", bookedBy);
            }
            
            return scheduleStr;
            
        } catch (SQLException e) {
            System.err.println("Error formatting schedule: " + e.getMessage());
            return "Schedule to be announced";
        }
    }
} 