package asm.org.MusicStudio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.User;

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
                String schedule = formatSchedule(rs);
                
                User teacher = new User();
                teacher.setId(rs.getInt("teacher_id"));
                teacher.setName(rs.getString("teacher_name"));
                
                Course course = Course.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .teacher(teacher)
                    .description(rs.getString("description"))
                    .monthlyFee(rs.getDouble("monthly_fee"))
                    .maxStudents(rs.getInt("max_students"))
                    .enrolledCount(rs.getInt("enrolled_count"))
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
                    User teacher = new User();
                    teacher.setId(rs.getInt("teacher_id"));
                    teacher.setName(rs.getString("teacher_name"));
                    
                    Course course = Course.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .teacher(teacher)
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
                   (SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.id) as enrolled_count
            FROM courses c
            LEFT JOIN users u ON c.teacher_id = u.id
            WHERE c.teacher_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            System.out.println("CourseDAO: Executing SQL with teacherId = " + teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User teacher = new User();
                teacher.setId(rs.getInt("teacher_id"));
                teacher.setName(rs.getString("teacher_name"));
                teacher.setEmail(rs.getString("teacher_email"));
                
                Course course = Course.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .teacher(teacher)
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