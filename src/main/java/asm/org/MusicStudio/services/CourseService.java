package asm.org.MusicStudio.services;

import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.entity.Course;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO = new CourseDAO();
    public List<Course> getAvailableCourses() throws SQLException {
        return courseDAO.findAllActiveCourses();
   
    }

    
    public void enrollStudent(Course course, LocalDate startDate, int duration) throws SQLException {
        // Implement enrollment logic
        // This is a placeholder - implement actual database access
    }
} 