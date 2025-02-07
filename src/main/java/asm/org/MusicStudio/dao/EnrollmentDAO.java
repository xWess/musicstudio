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
    // Remove or comment out these methods as they're not needed for the students view:
    // save()
    // createEnrollment()
    // findById()
    // updateEnrollment()
    // deleteEnrollmentById()
    // getAllEnrollments()

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
            WHERE c.teacher_id = ? AND u.role = 'STUDENT'
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
}