package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.dao.UserDAO;
import java.util.List;
import java.sql.SQLException;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.EnrollmentServiceImpl;
import asm.org.MusicStudio.services.EnrollmentService;
import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.dao.EnrollmentDAO;
import asm.org.MusicStudio.dao.StudentDAO;

public class StudentServiceImpl implements StudentService {
    private final UserDAO userDAO;
    private final CourseDAO courseDAO;
    private final EnrollmentService enrollmentService;
    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    
    public StudentServiceImpl() {
        this.userDAO = new UserDAO();
        this.courseDAO = new CourseDAO();
        this.enrollmentService = new EnrollmentServiceImpl();
        this.enrollmentDAO = new EnrollmentDAO();
        this.studentDAO = new StudentDAO();
    }
    

    @Override
    public Student getStudentProfile(Integer studentId) {
        try {
            User user = userDAO.findById(studentId.longValue());
            if (user instanceof Student) {
                return (Student) user;
            }
            throw new RuntimeException("User with ID " + studentId + " is not a student");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching student profile", e);
        }
    }
    
    @Override
    public void updateStudentProfile(Student student) {
        try {
            userDAO.updateUser(student);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating student profile", e);
        }
    }
    
    @Override
    public List<Course> getAvailableCourses() throws SQLException {
        return courseDAO.findAllActiveCourses();
    }

    public List<Enrollment> getCurrentEnrollments(Student student) {
        try {
            return enrollmentService.findCurrentEnrollmentsByStudent(student.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching current enrollments", e);
        }
    }
    
    public List<Enrollment> getEnrollmentHistory(Student student) {
        try {
            return enrollmentService.findAllEnrollmentsByStudent(student.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching enrollment history", e);
        }
    }
    
    public boolean canEnrollInCourse(Student student, Course course) {
        try {
            return enrollmentService.canEnroll(student, course);
        } catch (SQLException e) {
            throw new RuntimeException("Error checking enrollment eligibility", e);
        }
    }

    public Student getStudentByUserId(int userId) {
        try {
            // Query to get student data based on user ID
            String query = "SELECT * FROM students WHERE user_id = ?";
            
            // ... implementation details
            return studentDAO.findById(userId);
        } catch (SQLException e) {
            System.out.println("Error getting student by user ID: " + e.getMessage());
            return null;
        }
    }
} 