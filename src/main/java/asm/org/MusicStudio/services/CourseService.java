package asm.org.MusicStudio.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.entity.Course;

public class CourseService {
    public List<Course> getAvailableCourses() throws SQLException {
        // Implement course retrieval from database
        // This is a placeholder - implement actual database access
        return List.of(); // Return empty list for now
    }
    
    public void enrollStudent(Course course, LocalDate startDate, int duration) throws SQLException {
        // Implement enrollment logic
        // This is a placeholder - implement actual database access
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        System.out.println("CourseService: Getting courses for teacher " + teacherId);
        try {
            CourseDAO courseDAO = new CourseDAO();
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