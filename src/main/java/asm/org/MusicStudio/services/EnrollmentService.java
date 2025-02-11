package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.*;
import java.time.LocalDate;
import java.util.List;
import java.sql.SQLException;

public interface EnrollmentService {
    /**
     * Creates a new enrollment with payment
     */
    void createEnrollment(Student student, Course course, 
            LocalDate startDate, Payment payment) throws SQLException;
    
    /**
     * Updates enrollment status
     */
    void updateEnrollmentStatus(Enrollment enrollment, String newStatus) 
            throws SQLException;
            
    /**
     * Finds current enrollments for a student
     */
    List<Enrollment> findCurrentEnrollmentsByStudent(Integer studentId) 
            throws SQLException;
            
    /**
     * Finds all enrollments for a student
     */
    List<Enrollment> findAllEnrollmentsByStudent(Integer studentId) 
            throws SQLException;
            
    /**
     * Checks if a student can enroll in a course
     */
    boolean canEnroll(Student student, Course course) throws SQLException;
} 