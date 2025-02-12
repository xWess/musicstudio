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
import asm.org.MusicStudio.entity.Role;

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
        String sql = "SELECT id, name, teacher_id FROM courses WHERE teacher_id = ?";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course course = Course.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .teacherId(rs.getInt("teacher_id"))
                        .build();
                    courses.add(course);
                }
            }
        }
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

    public List<Course> findAvailableCourses() throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name, u.email as teacher_email,
                   (SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.id) as enrollment_count
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            WHERE c.status = 'ACTIVE'
            AND (SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.id) < c.capacity
        """;
        
        List<Course> courses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }
        return courses;
    }

    public void incrementEnrollmentCount(int courseId) throws SQLException {
        String sql = "UPDATE courses SET current_enrollment = current_enrollment + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
        }
    }

    public Course findById(Integer id) throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name, u.email as teacher_email
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            WHERE c.id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        }
        return null;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        User teacher = new User();
        teacher.setId(rs.getInt("teacher_id"));
        teacher.setName(rs.getString("teacher_name"));
        teacher.setEmail(rs.getString("teacher_email"));
        teacher.setRole(Role.TEACHER);

        return Course.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .teacher(teacher)
            .monthlyFee(rs.getDouble("monthly_fee"))
            .maxStudents(rs.getInt("max_students"))
            .enrolledCount(rs.getInt("enrolled_count"))
            .status(rs.getString("status"))
            .build();
    }
} 