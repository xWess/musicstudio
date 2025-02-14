package asm.org.MusicStudio.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Student;

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
    
    public Course findById(Integer id) throws SQLException {
        return courseDAO.findById(id);
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

    public List<Course> getCoursesByTeacher(Integer teacherId) throws SQLException {
        return courseDAO.findByTeacherId(teacherId);
    }

    public List<Student> getEnrolledStudents(Integer courseId) throws SQLException {
        String sql = """
            SELECT u.*, e.start_date as enrollment_date, e.status
            FROM users u
            JOIN enrollments e ON u.id = e.student_id
            WHERE e.course_id = ? AND e.status = 'ACTIVE'
            ORDER BY u.name
            """;
            
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setEmail(rs.getString("email"));
                student.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
                student.setStatus(rs.getString("status"));
                students.add(student);
            }
        }
        
        return students;
    }
} 