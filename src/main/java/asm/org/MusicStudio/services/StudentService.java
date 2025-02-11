package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Enrollment;
import java.sql.SQLException;
import java.util.List;

public interface StudentService {
    // Student Profile Management
    Student getStudentProfile(Integer studentId);
    void updateStudentProfile(Student student);
    
    // Course-related operations
    List<Course> getAvailableCourses() throws SQLException;
    
    // Enrollment operations (delegating to EnrollmentService internally)
    List<Enrollment> getCurrentEnrollments(Student student);
    List<Enrollment> getEnrollmentHistory(Student student);
    boolean canEnrollInCourse(Student student, Course course);

    Student getStudentByUserId(int userId);
} 