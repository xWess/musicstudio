package asm.org.MusicStudio.services;

import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.entity.Course;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CourseService {
    private static CourseService instance;
    private final CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
    }
    
    public static CourseService getInstance() {
        if (instance == null) {
            instance = new CourseService();
        }
        return instance;
    }

    public List<Course> getAvailableCourses() throws SQLException {
        return courseDAO.findAllActiveCourses();
    }

    public void enrollStudent(Course course, LocalDate startDate, int duration) throws SQLException {
        // Implement enrollment logic
        // This is a placeholder - implement actual database access
    }

    public void addCourse(Course course) throws SQLException {
        courseDAO.saveCourse(course);
    }

    public void deleteCourse(int courseId) throws SQLException {
        courseDAO.deleteCourse(courseId);
    }

    public void updateCourse(Course course) throws SQLException {
        courseDAO.updateCourse(course);
    }

    public List<Course> getAllActiveCourses() {
        try {
            return courseDAO.findAllActiveCourses();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get active courses", e);
        }
    }
} 