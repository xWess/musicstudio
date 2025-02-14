package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> findAllActiveCourses() throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name 
            FROM courses c 
            JOIN users u ON c.teacher_id = u.id
            """;
            
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(buildCourseFromResultSet(rs));
            }
        }
        
        return courses;
    }

    public List<Course> findByTeacherId(Integer teacherId) throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name 
            FROM courses c 
            JOIN users u ON c.teacher_id = u.id 
            WHERE c.teacher_id = ?
            """;
            
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                courses.add(buildCourseFromResultSet(rs));
            }
        }
        
        return courses;
    }

    public Course findById(Integer id) throws SQLException {
        String sql = """
            SELECT c.*, u.name as teacher_name 
            FROM courses c
            JOIN users u ON c.teacher_id = u.id
            WHERE c.id = ?
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return buildCourseFromResultSet(rs);
            }
            return null;
        }
    }

    public void saveCourse(Course course) throws SQLException {
        String sql = """
            INSERT INTO courses (name, teacher_id, schedule)
            VALUES (?, ?, ?)
            """;
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getTeacherId());
            stmt.setString(3, course.getSchedule());
            
            stmt.executeUpdate();
        }
    }

    public void updateCourse(Course course) throws SQLException {
        String sql = """
            UPDATE courses 
            SET name = ?, schedule = ?
            WHERE id = ?
            """;
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getName());
            stmt.setString(2, course.getSchedule());
            stmt.setInt(3, course.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public boolean hasAvailableSlots(Integer courseId) throws SQLException {
        String sql = """
            SELECT COUNT(*) as student_count 
            FROM enrollments 
            WHERE course_id = ? AND status = 'ACTIVE'
            """;
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int currentStudents = rs.getInt("student_count");
                Course course = findById(courseId);
                return course != null && (course.getMaxStudents() == null || currentStudents < course.getMaxStudents());
            }
            return false;
        }
    }

    private Course buildCourseFromResultSet(ResultSet rs) throws SQLException {
        return Course.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("name"))
            .teacherId(rs.getInt("teacher_id"))
            .teacherName(rs.getString("teacher_name"))
            .schedule(rs.getString("schedule"))
            .build();
    }
}