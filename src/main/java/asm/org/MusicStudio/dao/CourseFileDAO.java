package asm.org.MusicStudio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.constants.FileManagementConstants;
import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.entity.User;

public class CourseFileDAO {
    
    public void insertFile(CourseFile file) throws SQLException {
        // Add validation before insert
        if (file.getFileSize() > FileManagementConstants.MAX_FILE_SIZE) {
            throw new IllegalArgumentException(FileManagementConstants.ERROR_FILE_TOO_LARGE);
        }
        
        if (!isValidFileType(file.getFileType())) {
            throw new IllegalArgumentException(FileManagementConstants.ERROR_INVALID_FILE_TYPE);
        }
        
        String sql = """
            INSERT INTO course_files (file_name, file_path, course_id, teacher_id,
                                    file_type, description, file_size)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, file.getFileName());
            stmt.setString(2, file.getFilePath());
            stmt.setInt(3, file.getCourseId());
            stmt.setInt(4, file.getTeacherId());
            stmt.setString(5, file.getFileType());
            stmt.setString(6, file.getDescription());
            stmt.setLong(7, file.getFileSize());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    file.setId(rs.getInt(1));
                }
            }
        }
    }
    
    private boolean isValidFileType(String fileType) {
        return fileType != null && (
            fileType.equals(FileManagementConstants.FILE_TYPE_PDF) ||
            fileType.equals(FileManagementConstants.FILE_TYPE_DOC) ||
            fileType.equals(FileManagementConstants.FILE_TYPE_DOCX) ||
            fileType.equals(FileManagementConstants.FILE_TYPE_TXT)
        );
    }
    
    public List<CourseFile> findByCourseId(int courseId) throws SQLException {
        String sql = "SELECT * FROM course_files WHERE course_id = ?";
        List<CourseFile> files = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    files.add(mapResultSetToFile(rs));
                }
            }
        }
        return files;
    }
    
    public List<CourseFile> findByTeacherId(int teacherId) throws SQLException {
        String sql = """
            SELECT cf.*, c.name as course_name, u.name as teacher_name 
            FROM course_files cf
            JOIN courses c ON cf.course_id = c.id
            JOIN users u ON cf.teacher_id = u.id
            WHERE cf.teacher_id = ?
        """;
        
        List<CourseFile> files = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CourseFile file = mapResultSetToFile(rs);
                    // Set additional joined data
                    file.setCourse(Course.builder()
                        .id(rs.getInt("course_id"))
                        .name(rs.getString("course_name"))
                        .build());
                    file.setTeacher(User.builder()
                        .id(rs.getInt("teacher_id"))
                        .name(rs.getString("teacher_name"))
                        .build());
                    files.add(file);
                }
            }
        }
        return files;
    }
    
    public void deleteFile(int fileId) throws SQLException {
        String sql = "DELETE FROM course_files WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fileId);
            stmt.executeUpdate();
        }
    }
    
    public List<Integer> findCourseIdsByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT DISTINCT course_id FROM course_files WHERE teacher_id = ?";
        List<Integer> courseIds = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courseIds.add(rs.getInt("course_id"));
                }
            }
        }
        return courseIds;
    }
    
    public List<CourseFile> getFilesByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT * FROM course_files WHERE teacher_id = ?";
        List<CourseFile> files = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    files.add(mapResultSetToFile(rs));
                }
            }
        }
        return files;
    }

    private CourseFile mapResultSetToFile(ResultSet rs) throws SQLException {
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
            .build();
    }

    public CourseFile findById(int fileId) throws SQLException {
        String sql = """
            SELECT cf.*, c.name as course_name, u.name as teacher_name 
            FROM course_files cf
            LEFT JOIN courses c ON cf.course_id = c.id
            LEFT JOIN users u ON cf.teacher_id = u.id
            WHERE cf.id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, fileId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CourseFile file = mapResultSetToFile(rs);
                    // Set additional joined data if available
                    if (rs.getString("course_name") != null) {
                        file.setCourse(Course.builder()
                            .id(rs.getInt("course_id"))
                            .name(rs.getString("course_name"))
                            .build());
                    }
                    if (rs.getString("teacher_name") != null) {
                        file.setTeacher(User.builder()
                            .id(rs.getInt("teacher_id"))
                            .name(rs.getString("teacher_name"))
                            .build());
                    }
                    return file;
                }
            }
        }
        return null;
    }
} 