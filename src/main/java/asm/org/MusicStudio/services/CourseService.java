package asm.org.MusicStudio.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.entity.Course;

public class CourseService {
    private CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
    }

    public List<Course> getAvailableCourses() throws SQLException {
        // Replace placeholder with actual implementation
        return courseDAO.findAvailableCourses();
    }
    
    public void enrollStudent(Course course, LocalDate startDate, int duration) throws SQLException {
        // Replace placeholder with actual implementation
        try {
            // Validate course exists
            Course existingCourse = courseDAO.findById(course.getId());
            if (existingCourse == null) {
                throw new RuntimeException("Course not found");
            }
            
            // Update course enrollment count
            courseDAO.incrementEnrollmentCount(course.getId());
            
            // Create enrollment record (handled by EnrollmentService)
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enroll student: " + e.getMessage(), e);
        }
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        System.out.println("CourseService: Getting courses for teacher " + teacherId);
        try {
            List<Course> courses = courseDAO.findByTeacherId(teacherId);
            System.out.println("CourseService: Found " + courses.size() + " courses");
            return courses;
        } catch (SQLException e) {
            System.err.println("CourseService Error: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
} 