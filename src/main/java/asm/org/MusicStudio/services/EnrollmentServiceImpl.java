package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.*;
import asm.org.MusicStudio.dao.*;
import java.time.LocalDate;
import java.util.List;
import java.sql.SQLException;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;
    
    public EnrollmentServiceImpl() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.courseDAO = new CourseDAO();
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
} 