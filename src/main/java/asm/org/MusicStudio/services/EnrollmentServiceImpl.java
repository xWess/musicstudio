package asm.org.MusicStudio.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.dao.EnrollmentDAO;
import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.Student;

public class EnrollmentServiceImpl implements EnrollmentService {
    private static EnrollmentServiceImpl instance;
    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;
    
    private EnrollmentServiceImpl() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.courseDAO = new CourseDAO();
    }

    public static EnrollmentServiceImpl getInstance() {
        if (instance == null) {
            synchronized (EnrollmentServiceImpl.class) {
                if (instance == null) {
                    instance = new EnrollmentServiceImpl();
                }
            }
        }
        return instance;
    }
    
    @Override
    public void createEnrollment(Student student, Course course, 
            LocalDate startDate, Payment payment) throws SQLException {
        // Validate enrollment
        if (!canEnroll(student, course)) {
            throw new IllegalStateException("Student cannot enroll in this course");
        }
        
        Enrollment enrollment = Enrollment.builder()
            .student(student)
            .course(course)
            .startDate(startDate)
            .endDate(startDate.plusMonths(3))
            .status("ACTIVE")
            .payment(payment)
            .build();
            
        enrollmentDAO.save(enrollment);
    }
    
    @Override
    public void updateEnrollmentStatus(Enrollment enrollment, String newStatus) 
            throws SQLException {
        // Validate status transition
        if (!isValidStatusTransition(enrollment.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition from " 
                + enrollment.getStatus() + " to " + newStatus);
        }
        
        enrollment.setStatus(newStatus);
        enrollmentDAO.updateEnrollment(enrollment);
    }
    
    @Override
    public List<Enrollment> findCurrentEnrollmentsByStudent(Integer studentId) 
            throws SQLException {
        return enrollmentDAO.findCurrentEnrollmentsByStudent(studentId);
    }
    
    @Override
    public List<Enrollment> findAllEnrollmentsByStudent(Integer studentId) 
            throws SQLException {
        return enrollmentDAO.findAllEnrollmentsByStudent(studentId);
    }
    
    @Override
    public boolean canEnroll(Student student, Course course) throws SQLException {
        return courseDAO.hasAvailableSlots(course.getId()) &&
               !isAlreadyEnrolled(student, course);
    }
    
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case "ACTIVE":
                return newStatus.equals("COMPLETED") || 
                       newStatus.equals("CANCELLED") ||
                       newStatus.equals("SUSPENDED");
            case "SUSPENDED":
                return newStatus.equals("ACTIVE") || 
                       newStatus.equals("CANCELLED");
            default:
                return false;
        }
    }
    
    private boolean isAlreadyEnrolled(Student student, Course course) 
            throws SQLException {
        List<Enrollment> currentEnrollments = 
            findCurrentEnrollmentsByStudent(student.getId());
        return currentEnrollments.stream()
            .anyMatch(e -> e.getCourse().getId().equals(course.getId()));
    }

    public void createEnrollment(Enrollment enrollment) {
        try {
            createEnrollment(
                (Student) enrollment.getStudent(),
                (Course) enrollment.getCourse(),
                enrollment.getStartDate(),
                (Payment) enrollment.getPayment()
            );

        } catch (SQLException e) {
            throw new RuntimeException("Error creating enrollment", e);
        }
    }

    @Override
    public List<Enrollment> getEnrollmentsByTeacher(int teacherId) throws SQLException {
        String sql = """
            SELECT e.*, c.*, s.* 
            FROM enrollments e
            JOIN courses c ON e.course_id = c.id
            JOIN users s ON e.student_id = s.id
            WHERE c.teacher_id = ?
            """;
            
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("student_id"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setRole(Role.STUDENT);
                
                Course course = Course.builder()
                    .id(rs.getInt("course_id"))
                    .name(rs.getString("name"))
                    .build();
                    
                Enrollment enrollment = Enrollment.builder()
                    .id(rs.getInt("id"))
                    .student(student)
                    .course(course)
                    .startDate(rs.getDate("start_date").toLocalDate())
                    .status(rs.getString("status"))
                    .build();
                    
                enrollments.add(enrollment);
            }
        }
        return enrollments;
    }
} 