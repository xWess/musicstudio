package asm.org.MusicStudio.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.dao.EnrollmentDAO;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.Student;

public class EnrollmentService {
    
    private EnrollmentDAO enrollmentDAO;

    public EnrollmentService() {
        this.enrollmentDAO = new EnrollmentDAO();
    }

    /**
     * Gets all enrollments for the current user
     * @param semester Filter by semester (optional)
     * @return List of enrollments
     */
    public List<Enrollment> getEnrollments(String semester) {
        try {
            // Change from findByTeacherId to findByInstructorId
            return enrollmentDAO.findByInstructorId(getCurrentUserId());
        } catch (SQLException e) {
            System.err.println("Error fetching enrollments: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Enrolls a student in a course
     * @param enrollment The enrollment details
     */
    public void enrollInCourse(Enrollment enrollment) {
        try {
            // Validate course capacity
            if (!checkCourseAvailability(enrollment.getCourse().getId().longValue())) {
                throw new RuntimeException("Course is at full capacity");
            }
            
            // Create enrollment record
            enrollmentDAO.save(enrollment);
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enroll in course: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cancels an enrollment
     * @param enrollmentId The ID of the enrollment to cancel
     */
    public void cancelEnrollment(Long enrollmentId) {
        try {
            enrollmentDAO.deleteEnrollmentById(enrollmentId.intValue());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to cancel enrollment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Checks if a course has available slots
     * @param courseId The course to check
     * @return boolean indicating availability
     */
    public boolean checkCourseAvailability(Long courseId) {
        try {
            // Replace placeholder with actual capacity check
            String sql = """
                SELECT (c.max_students > (
                    SELECT COUNT(*) FROM enrollments e 
                    WHERE e.course_id = c.id AND e.status = 'ACTIVE'
                )) as has_capacity
                FROM courses c WHERE c.id = ?
            """;
            return enrollmentDAO.checkCourseAvailability(courseId.intValue());
        } catch (Exception e) {
            System.err.println("Error checking course availability: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets enrollments for a student
     * @param student The student
     * @return List of enrollments
     */
    public List<Enrollment> getStudentEnrollments(Student student) {
        try {
            // Replace placeholder implementation
            String sql = """
                SELECT e.*, c.name as course_name, c.instructor_id
                FROM enrollments e
                JOIN courses c ON e.course_id = c.id
                WHERE e.student_id = ?
            """;
            return enrollmentDAO.findByStudentId(student.getId());
        } catch (SQLException e) {
            System.err.println("Error fetching student enrollments: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Creates a new enrollment
     * @param student The enrolling student
     * @param course The course to enroll in
     * @param startDate Start date
     * @param payment Initial payment
     */
    public void createEnrollment(Student student, Course course, 
            LocalDate startDate, Payment payment) {
        try {
            Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .status("ACTIVE")
                .build();
                
            enrollmentDAO.createEnrollment(enrollment);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create enrollment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates enrollment status
     * @param enrollment The enrollment to update
     * @param newStatus New status
     */
    public void updateEnrollmentStatus(Enrollment enrollment, String newStatus) {
        try {
            enrollment.setStatus(newStatus);
            enrollmentDAO.updateEnrollment(enrollment);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update enrollment status: " + e.getMessage(), e);
        }
    }

    public List<Enrollment> getEnrollmentsByInstructor(int instructorId) {
        try {
            return enrollmentDAO.findByInstructorId(instructorId);
        } catch (SQLException e) {
            System.err.println("Error fetching instructor enrollments: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private int getCurrentUserId() {
        // TODO: Implement proper user session management
        return 1; // Temporary default value
    }
} 