package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Teacher;
import asm.org.MusicStudio.entity.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {
    public void createUser(User user) throws SQLException { 
        String sql = "INSERT INTO users (name, email, role) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().toString());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    switch (Role.valueOf(role.toUpperCase())) {
                        case ARTIST:
                            return new Artist(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                role);
                        case TEACHER:
                            return new Teacher(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                role);
                        case STUDENT:
                            return new Student(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                role);
                        default:
                            throw new IllegalStateException("Unexpected role: " + role);
                    }
                }
            }
        }
        return null;
    }
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().toString());
            pstmt.setInt(4, user.getId());

            pstmt.executeUpdate();
        }
    }


    public void deleteUserById(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String role = rs.getString("role");
                switch (Role.valueOf(role.toUpperCase())) {
                    case ARTIST:
                        users.add(new Artist(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            role));
                        break;
                    case TEACHER:
                        users.add(new Teacher(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            role));
                        break;
                    case STUDENT:
                        users.add(new Student(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            role));
                        break;
                    default:
                        throw new IllegalStateException("Unexpected role: " + role);
                }
            }
        }
        return users;
    }
} 