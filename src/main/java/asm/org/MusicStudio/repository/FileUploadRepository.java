package asm.org.MusicStudio.repository;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.constants.FileUploadConstants;
import asm.org.MusicStudio.mapper.FileUploadMapper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileUploadRepository {
    private static FileUploadRepository instance;
    
    public static FileUploadRepository getInstance() {
        if (instance == null) {
            instance = new FileUploadRepository();
        }
        return instance;
    }
    
    public CourseFile save(CourseFile file) throws SQLException {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(FileUploadQueries.INSERT_FILE)) {
            
            stmt.setInt(1, file.getCourseId());
            stmt.setInt(2, file.getTeacherId());
            stmt.setString(3, file.getFileName());
            stmt.setString(4, file.getFilePath());
            stmt.setString(5, file.getFileType());
            stmt.setLong(6, file.getFileSize());
            stmt.setString(7, file.getDescription());
            stmt.setString(8, FileUploadConstants.DEFAULT_STATUS);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                file.setId(rs.getInt(FileUploadConstants.Columns.ID));
            }
        }
        return file;
    }
    
    public void delete(Integer fileId) throws SQLException {
        String sql = "DELETE FROM course_files WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fileId);
            stmt.executeUpdate();
        }
    }
    
    public List<CourseFile> findByTeacherId(Integer teacherId) throws SQLException {
        String sql = """
            SELECT cf.*, c.name as course_name, u.name as teacher_name
            FROM course_files cf
            JOIN courses c ON cf.course_id = c.id
            JOIN users u ON cf.teacher_id = u.id
            WHERE cf.teacher_id = ? AND cf.status = 'ACTIVE'
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
        }
        return files;
    }
    
    private CourseFile buildCourseFileFromResultSet(ResultSet rs) throws SQLException {
        return CourseFile.builder()
            .id(rs.getInt("id"))
            .fileName(rs.getString("file_name"))
            .filePath(rs.getString("file_path"))
            .uploadDate(rs.getTimestamp("upload_date").toLocalDateTime())
            .courseId(rs.getInt("course_id"))
            .teacherId(rs.getInt("teacher_id"))
            .fileType(rs.getString("file_type"))
            .description(rs.getString("description"))
            .fileSize(rs.getLong("file_size"))
            .courseName(rs.getString("course_name"))
            .teacherName(rs.getString("teacher_name"))
            .build();
    }
} 