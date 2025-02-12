package asm.org.MusicStudio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.User;

public class EnrollmentDAO {
    public List<Enrollment> findByTeacherId(int teacherId) throws SQLException {
        System.out.println("Finding enrollments for teacher ID: " + teacherId);
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = """
            SELECT e.id as enrollment_id, 
                   u.id as student_id, 
                   u.name as student_name, 
                   u.email as student_email,
                   c.id as course_id, 
                   c.name as course_name,
                   c.teacher_id
            FROM enrollments e
            JOIN users u ON e.student_id = u.id
            JOIN courses c ON e.course_id = c.id
            WHERE c.teacher_id = ? AND LOWER(u.role) = 'student'
            ORDER BY u.name
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            System.out.println("Executing query: " + sql);
            System.out.println("With teacher ID: " + teacherId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                String studentName = rs.getString("student_name");
                String studentEmail = rs.getString("student_email");
                int courseId = rs.getInt("course_id");
                String courseName = rs.getString("course_name");
                
                System.out.println("Found record:");
                System.out.println("- Student ID: " + studentId);
                System.out.println("- Student Name: " + studentName);
                System.out.println("- Course ID: " + courseId);
                System.out.println("- Course Name: " + courseName);

                User student = new User();
                student.setId(studentId);
                student.setName(studentName);
                student.setEmail(studentEmail);
                student.setRole(Role.STUDENT);
                
                // Create teacher object from result set
                User teacher = new User();
                teacher.setId(rs.getInt("teacher_id"));
                teacher.setRole(Role.TEACHER);
                
                Course course = Course.builder()
                    .id(rs.getInt("course_id"))
                    .name(rs.getString("course_name"))
                    .teacher(teacher)  // Pass the teacher object
                    .build();
                
                Enrollment enrollment = Enrollment.builder()
                    .id(rs.getInt("enrollment_id"))
                    .student(student)
                    .course(course)
                    .build();
                    
                System.out.println("Created enrollment object for: " + student.getName());
                enrollments.add(enrollment);
            }
            
            System.out.println("Total enrollments found: " + enrollments.size());
        }
        return enrollments;
    }

    public void save(Enrollment enrollment) throws SQLException {
        String sql = """
            INSERT INTO enrollments (student_id, course_id, status)
            VALUES (?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollment.getStudent().getId());
            stmt.setInt(2, enrollment.getCourse().getId());
            stmt.setString(3, enrollment.getStatus());
            
            stmt.executeUpdate();
        }
    }

    public void deleteByStudentAndCourse(int studentId, int courseId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            
            stmt.executeUpdate();
        }
    }

    public void createEnrollment(Enrollment enrollment) throws SQLException {
        String sql = """
            INSERT INTO enrollments (student_id, course_id, status)
            VALUES (?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollment.getStudent().getId());
            stmt.setInt(2, enrollment.getCourse().getId());
            stmt.setString(3, enrollment.getStatus());
            
            stmt.executeUpdate();
        }
    }

    public Enrollment findById(int enrollmentId) throws SQLException {
        String sql = """
            SELECT e.*, u.name as student_name, u.email as student_email,
                   c.name as course_name, c.teacher_id
            FROM enrollments e
            JOIN users u ON e.student_id = u.id
            JOIN courses c ON e.course_id = c.id
            WHERE e.id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEnrollment(rs);
            }
        }
        return null;
    }

    public void updateEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, enrollment.getStatus());
            stmt.setInt(2, enrollment.getId());
            
            stmt.executeUpdate();
        }
    }

    public void deleteEnrollmentById(int enrollmentId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate();
        }
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        String sql = """
            SELECT e.*, u.name as student_name, u.email as student_email,
                   c.name as course_name, c.teacher_id
            FROM enrollments e
            JOIN users u ON e.student_id = u.id
            JOIN courses c ON e.course_id = c.id
        """;
        
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
        }
        return enrollments;
    }

    public List<Enrollment> findByStudentId(int studentId) throws SQLException {
        String sql = """
            SELECT e.*, u.name as student_name, u.email as student_email,
                   c.name as course_name, c.teacher_id
            FROM enrollments e
            JOIN users u ON e.student_id = u.id
            JOIN courses c ON e.course_id = c.id
            WHERE e.student_id = ?
        """;
        
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
        }
        return enrollments;
    }

    public boolean checkCourseAvailability(int courseId) throws SQLException {
        String sql = """
            SELECT (c.max_students > (
                SELECT COUNT(*) FROM enrollments e 
                WHERE e.course_id = c.id AND e.status = 'ACTIVE'
            )) as has_capacity
            FROM courses c WHERE c.id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean("has_capacity");
        }
    }

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        User student = new User();
        student.setId(rs.getInt("student_id"));
        student.setName(rs.getString("student_name"));
        student.setEmail(rs.getString("student_email"));
        student.setRole(Role.STUDENT);
        
        User teacher = new User();
        teacher.setId(rs.getInt("teacher_id"));
        teacher.setRole(Role.TEACHER);
        
        Course course = Course.builder()
            .id(rs.getInt("course_id"))
            .name(rs.getString("course_name"))
            .teacher(teacher)
            .build();
        
        return Enrollment.builder()
            .id(rs.getInt("id"))
            .student(student)
            .course(course)
            .status(rs.getString("status"))
            .build();
    }
}