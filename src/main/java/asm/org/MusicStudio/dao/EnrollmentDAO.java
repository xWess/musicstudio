package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    public Enrollment save(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, start_date, end_date, status, payment_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, enrollment.getStudent().getId());
            pstmt.setInt(2, enrollment.getCourse().getId());
            pstmt.setDate(3, Date.valueOf(enrollment.getStartDate()));
            pstmt.setDate(4, Date.valueOf(enrollment.getEndDate()));
            pstmt.setString(5, enrollment.getStatus());
            pstmt.setLong(6, enrollment.getPayment().getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating enrollment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    enrollment.setId(generatedKeys.getInt(1));
                    return enrollment;
                } else {
                    throw new SQLException("Creating enrollment failed, no ID obtained.");
                }
            }
        }
    }

    public Enrollment findById(Long id) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student(); // You need to implement fetching student by ID
                    Course course = new Course(); // You need to implement fetching course by ID
                    return Enrollment.builder()
                            .id(rs.getInt("id"))
                            .student(student)
                            .course(course)
                            .build();
                }
            }
        }
        return null;
    }

    public void updateEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, enrollment.getStudent().getId());
            pstmt.setLong(2, enrollment.getCourse().getId());
            pstmt.setLong(3, enrollment.getId());

            pstmt.executeUpdate();
        }
    }

    public void deleteEnrollmentById(Long id) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        String sql = "SELECT * FROM enrollments";
        List<Enrollment> enrollments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(); // You need to implement fetching student by ID
                Course course = new Course(); // You need to implement fetching course by ID
                enrollments.add(Enrollment.builder()
                        .id(rs.getInt("id"))
                        .student(student)
                        .course(course)
                        .build());
            }
        }
        return enrollments;
    }

    public List<Enrollment> findCurrentEnrollmentsByStudent(Integer studentId) throws SQLException {
        String sql = """
            SELECT e.*, c.id as course_id, c.name, c.description,
                   u.name as teacher_name, e.start_date, e.end_date, e.status
            FROM enrollments e
            JOIN courses c ON e.course_id = c.id
            JOIN users u ON c.teacher_id = u.id
            WHERE e.student_id = ? AND e.status = 'ACTIVE'
            AND e.end_date >= CURRENT_DATE
        """;
        
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = Course.builder()
                        .id(rs.getInt("course_id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .instructor(rs.getString("teacher_name"))
                        .build();
                    
                    Enrollment enrollment = Enrollment.builder()
                        .id(rs.getInt("id"))
                        .student(new Student(studentId, null, null))
                        .course(course)
                        .startDate(rs.getDate("start_date").toLocalDate())
                        .endDate(rs.getDate("end_date").toLocalDate())
                        .status(rs.getString("status"))
                        .build();
                    
                    enrollments.add(enrollment);
                }
            }
        }
        return enrollments;
    }

    public List<Enrollment> findAllEnrollmentsByStudent(Integer studentId) throws SQLException {
        String sql = """
            SELECT e.*, c.*, u.name as teacher_name 
            FROM enrollments e
            JOIN courses c ON e.course_id = c.id
            JOIN users u ON c.teacher_id = u.id
            WHERE e.student_id = ?
            ORDER BY e.start_date DESC
        """;
        
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = Course.builder()
                        .id(rs.getInt("course_id"))
                        .name(rs.getString("name"))
                        .instructor(rs.getString("teacher_name"))
                        .schedule(rs.getString("schedule"))
                        .build();
                    
                    Enrollment enrollment = Enrollment.builder()
                        .id(rs.getInt("id"))
                        .student(new Student(studentId, null, null))
                        .course(course)
                        .startDate(rs.getDate("start_date").toLocalDate())
                        .endDate(rs.getDate("end_date").toLocalDate())
                        .status(rs.getString("status"))
                        .build();
                    
                    enrollments.add(enrollment);
                }
            }
        }
        return enrollments;
    }
}