package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    public void createEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, enrollment.getStudent().getId());
            pstmt.setLong(2, enrollment.getCourse().getId());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    enrollment.setId(generatedKeys.getInt(1));
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

    public Enrollment save(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, start_date, end_date, status, payment_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
                    
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, enrollment.getStudent().getId());
            pstmt.setInt(2, enrollment.getCourse().getId());
            pstmt.setDate(3, Date.valueOf(enrollment.getStartDate()));
            pstmt.setDate(4, Date.valueOf(enrollment.getEndDate()));
            pstmt.setString(5, enrollment.getStatus());
            pstmt.setLong(6, enrollment.getPayment().getId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    enrollment.setId(rs.getInt("id"));
                    return enrollment;
                }
            }
        }
        throw new SQLException("Failed to save enrollment");
    }
}