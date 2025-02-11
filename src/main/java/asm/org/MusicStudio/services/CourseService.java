package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.dao.CourseDAO;
import java.sql.SQLException;
import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO;
    
    public CourseService() {
        this.courseDAO = new CourseDAO();
    }

    public List<Course> getAvailableCourses() throws SQLException {
        return courseDAO.findAvailableCourses();
    }
    
    public Course findById(Integer id) throws SQLException {
        return courseDAO.findById(id);
    }
} 