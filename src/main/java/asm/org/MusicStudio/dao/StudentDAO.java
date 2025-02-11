package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Role;

import java.sql.*;

public class StudentDAO {
    
    public Student findById(int userId) throws SQLException {
        String sql = """
            SELECT * FROM users 
            WHERE id = ? AND role = 'STUDENT'
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }
    
    public void save(Student student) throws SQLException {
        String sql = """
            INSERT INTO users (name, email, role, password, active) 
            VALUES (?, ?, 'STUDENT', ?, true)
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPassword());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public void update(Student student) throws SQLException {
        String sql = """
            UPDATE users 
            SET name = ?, email = ?, password = ?
            WHERE id = ? AND role = 'STUDENT'
        """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPassword());
            pstmt.setInt(4, student.getId());
            
            pstmt.executeUpdate();
        }
    }
}