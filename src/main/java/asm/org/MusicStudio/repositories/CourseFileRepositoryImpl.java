package asm.org.MusicStudio.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.CourseFile;

public class CourseFileRepositoryImpl implements CourseFileRepository {
    
    @Override
    public void save(CourseFile file) {
        String sql = """
            INSERT INTO course_files (file_name, file_path, file_type, file_size, description, course_id, teacher_id, upload_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
            """;
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, file.getFileName());
            stmt.setString(2, file.getFilePath());
            stmt.setString(3, file.getFileType());
            stmt.setLong(4, file.getFileSize());
            stmt.setString(5, file.getDescription());
            stmt.setInt(6, file.getCourseId());
            stmt.setInt(7, file.getTeacherId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving course file", e);
        }
    }

    @Override
    public void delete(CourseFile file) {
        String sql = "DELETE FROM course_files WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, file.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting course file", e);
        }
    }

    @Override
    public CourseFile findById(Long id) {
        String sql = "SELECT * FROM course_files WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return buildCourseFileFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course file by id", e);
        }
    }

    @Override
    public List<CourseFile> findByTeacherId(Integer teacherId) {
        String sql = """
            SELECT cf.*, c.name as course_name, u.name as teacher_name 
            FROM course_files cf
            JOIN courses c ON cf.course_id = c.id
            JOIN users u ON cf.teacher_id = u.id
            WHERE cf.teacher_id = ?
            ORDER BY cf.upload_date DESC
            """;
        List<CourseFile> files = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                files.add(buildCourseFileFromResultSet(rs));
            }
            return files;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course files by teacher id", e);
        }
    }

    public List<CourseFile> findByCourseIdForStudent(Integer courseId) {
        String sql = """
            SELECT cf.*, c.name as course_name, u.name as teacher_name 
            FROM course_files cf
            JOIN courses c ON cf.course_id = c.id
            JOIN users u ON cf.teacher_id = u.id
            WHERE cf.course_id = ?
            ORDER BY cf.upload_date DESC
            """;
        List<CourseFile> files = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                files.add(buildCourseFileFromResultSet(rs));
            }
            return files;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course files by course id", e);
        }
    }

    @Override
    public List<CourseFile> findByStudentEnrollment(Integer studentId) {
        String sql = """
            SELECT cf.*, c.name as course_name, u.name as teacher_name 
            FROM course_files cf
            JOIN courses c ON cf.course_id = c.id
            JOIN users u ON cf.teacher_id = u.id
            JOIN enrollments e ON e.course_id = c.id
            WHERE e.student_id = ? AND e.status = 'ACTIVE'
            ORDER BY cf.upload_date DESC
            """;
        List<CourseFile> files = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                files.add(buildCourseFileFromResultSet(rs));
            }
            return files;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding course files for student", e);
        }
    }

    private CourseFile buildCourseFileFromResultSet(ResultSet rs) throws SQLException {
        CourseFile file = CourseFile.builder()
            .id(rs.getInt("id"))
            .fileName(rs.getString("file_name"))
            .filePath(rs.getString("file_path"))
            .fileType(rs.getString("file_type"))
            .fileSize(rs.getLong("file_size"))
            .description(rs.getString("description"))
            .courseId(rs.getInt("course_id"))
            .courseName(rs.getString("course_name"))
            .teacherId(rs.getInt("teacher_id"))
            .teacherName(rs.getString("teacher_name"))
            .uploadDate(rs.getTimestamp("upload_date").toLocalDateTime())
            .build();
        
        // Mettre à jour les propriétés JavaFX
        file.setFileName(file.getFileName());
        file.setCourseName(file.getCourseName());
        file.setTeacherName(file.getTeacherName());
        file.setDescription(file.getDescription());
        
        return file;
    }
} 