package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.entity.*;
import asm.org.MusicStudio.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, role, password, active) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().toString());
            pstmt.setString(4, user.getPassword());
            pstmt.setBoolean(5, user.isActive());

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
                    return createUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, active = ?, " +
                    "last_login = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().toString());
            pstmt.setBoolean(5, user.isActive());
            
            if (user.getLastLogin() != null) {
                pstmt.setTimestamp(6, Timestamp.valueOf(user.getLastLogin()));
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }
            
            pstmt.setInt(7, user.getId());
            
            pstmt.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Role role = Role.valueOf(rs.getString("role").toUpperCase());
                User user = switch (role) {
                    case STUDENT -> new Student();
                    case TEACHER -> new Teacher();
                    case ARTIST -> new Artist();
                    case ADMIN -> new Admin();
                };

                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setRole(role);
                user.setPassword(rs.getString("password"));
                user.setActive(rs.getBoolean("active"));

                Timestamp lastLogin = rs.getTimestamp("last_login");
                if (lastLogin != null) {
                    user.setLastLogin(lastLogin.toLocalDateTime());
                }

                users.add(user);
            }
        }
        return users;
    }

    public User findByResetToken(String resetToken) throws SQLException {
        String sql = "SELECT * FROM users WHERE reset_token = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resetToken);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role, active) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().toString());
            pstmt.setBoolean(5, user.isActive());
            
            pstmt.executeUpdate();
        }
    }

    public List<User> findByRole(Role role) throws SQLException {
        String sql = "SELECT * FROM users WHERE TRIM(BOTH '\"' FROM role) = ?";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, role.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(createUserFromResultSet(rs));
                }
            }
        }
        return users;
    }

    public User findById(Long userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        String rawRole = rs.getString("role");
        System.out.println("Raw role from database: '" + rawRole + "'");
        
        // Clean and standardize the role string
        String roleStr = rawRole.replace("\"", "")
                               .trim()
                               .toUpperCase();
        System.out.println("Processed role string: '" + roleStr + "'");

        try {
            // Convert to Role enum
            Role userRole = Role.valueOf(roleStr);
            System.out.println("Converted to Role enum: " + userRole);
            
            // Create appropriate user type
            return switch (userRole) {
                case ADMIN -> {
                    Admin admin = new Admin();
                    populateUserFields(admin, rs, userRole);
                    yield admin;
                }
                case STUDENT -> {
                    Student student = new Student();
                    populateUserFields(student, rs, userRole);
                    yield student;
                }
                case TEACHER -> {
                    Teacher teacher = new Teacher();
                    populateUserFields(teacher, rs, userRole);
                    yield teacher;
                }
                case ARTIST -> {
                    Artist artist = new Artist();
                    populateUserFields(artist, rs, userRole);
                    yield artist;
                }
            };
        } catch (IllegalArgumentException e) {
            throw new SQLException("Unexpected role: " + roleStr, e);
        }
    }

    private void populateUserFields(User user, ResultSet rs, Role role) throws SQLException {
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(role);
        user.setActive(rs.getBoolean("active"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
    }
}