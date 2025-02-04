package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Course;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
} 