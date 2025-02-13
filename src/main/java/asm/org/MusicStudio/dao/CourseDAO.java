package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;
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
                    .instructor(createTeacherFromResultSet(rs))
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
                    .instructor(createTeacherFromResultSet(rs))
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
                        .instructor(createTeacherFromResultSet(rs))
                        .maxStudents(rs.getInt("max_students"))
                        .schedule(schedule)
                        .build();
                }
                return null; // Return null if no course found
            }
        }
    }

    public List<Course> findCoursesByTeacherId(Integer teacherId) throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name,
                   s.day_of_week, s.start_time, s.end_time, r.location
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            LEFT JOIN schedules s ON c.id = s.course_id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE c.teacher_id = ?
            ORDER BY c.name
        """;
        
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String schedule = buildScheduleString(rs);
                    Course course = Course.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .monthlyFee(rs.getDouble("monthly_fee"))
                        .instructor(createTeacherFromResultSet(rs))
                        .maxStudents(rs.getInt("max_students"))
                        .schedule(schedule)
                        .build();
                    
                    courses.add(course);
                }
            }
        }
        return courses;
    }

    public void assignRoomToCourse(Integer courseId, Integer roomId, 
                                 String dayOfWeek, Time startTime, Time endTime) throws SQLException {
        String sql = "INSERT INTO schedules (course_id, room_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, roomId);
            pstmt.setString(3, dayOfWeek);
            pstmt.setTime(4, startTime);
            pstmt.setTime(5, endTime);
            
            pstmt.executeUpdate();
        }
    }

    public void uploadCourseFile(Integer courseId, String fileName, byte[] fileData) throws SQLException {
        String sql = "INSERT INTO course_files (course_id, file_name, file_data) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.setString(2, fileName);
            pstmt.setBytes(3, fileData);
            
            pstmt.executeUpdate();
        }
    }

    public List<Course> findEnrolledStudentsCourses(Integer studentId) throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name,
                   s.day_of_week, s.start_time, s.end_time, r.location
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            JOIN enrollments e ON c.id = e.course_id
            LEFT JOIN schedules s ON c.id = s.course_id
            LEFT JOIN rooms r ON s.room_id = r.id
            WHERE e.student_id = ? AND e.status = 'ACTIVE'
            ORDER BY c.name
        """;
        
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String schedule = buildScheduleString(rs);
                    Course course = Course.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .monthlyFee(rs.getDouble("monthly_fee"))
                        .instructor(createTeacherFromResultSet(rs))
                        .maxStudents(rs.getInt("max_students"))
                        .schedule(schedule)
                        .build();
                    
                    courses.add(course);
                }
            }
        }
        return courses;
    }

    public void incrementEnrollmentCount(Integer courseId) throws SQLException {
        String sql = "UPDATE courses SET current_enrollment = current_enrollment + 1 WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            pstmt.executeUpdate();
        }
    }

    public List<Course> findByTeacherId(int teacherId) throws SQLException {
        return findCoursesByTeacherId(teacherId);
    }

    private User createTeacherFromResultSet(ResultSet rs) throws SQLException {
        User teacher = new User();
        teacher.setId(rs.getInt("teacher_id"));
        teacher.setName(rs.getString("teacher_name"));
        teacher.setRole(Role.TEACHER);
        return teacher;
    }
} 