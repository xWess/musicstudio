package asm.org.MusicStudio.services;

import asm.org.MusicStudio.dao.UserDAO;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;
import at.favre.lib.crypto.bcrypt.BCrypt;


public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    @Override
    public void addUser(User user) {
        try {
            userDAO.createUser(user);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        try {
            return userDAO.findByEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateUser(User user) {
        try {
            String query = "UPDATE users SET name = ?, email = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setInt(3, user.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to update user, no rows affected.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUserById(int userId) {
        try {
            userDAO.deleteUser(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            List<User> users = new ArrayList<>();
            String query = "SELECT * FROM users";
            
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    
                    String roleStr = rs.getString("role");
                    if (roleStr != null) {
                        try {
                            Role role = Role.fromString(roleStr);
                            user.setRole(role);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Invalid role found in database: " + roleStr);
                            user.setRole(Role.STUDENT);
                        }
                    }
                    users.add(user);
                }
            }
            return users;
            
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public User findUserByResetToken(String resetToken) {
        throw new UnsupportedOperationException("Password reset not implemented");
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        try {
            return userDAO.findByRole(role);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users by role: " + role, e);
        }
    }

    @Override
    public boolean isAuthorized(User user, Role requiredRole) {
        return user != null && user.getRole() == requiredRole;
    }

    @Override
    public void initiatePasswordReset(String email) {
        try {
            User user = userDAO.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("User not found with email: " + email);
            }
            // Generate reset token and save it
            String resetToken = generateResetToken();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
            userDAO.updateUser(user);
            // TODO: Send reset email with token
        } catch (SQLException e) {
            throw new RuntimeException("Error initiating password reset: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePassword(User user, String currentPassword, String newPassword) {
        try {
            if (!validateCredentials(user.getEmail(), currentPassword)) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, hashPassword(newPassword));
                stmt.setInt(2, user.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to update password, no rows affected.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            user.setPassword(hashPassword(newPassword));
            userDAO.updateUser(user);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateCredentials(String email, String password) {
        try {
            User user = userDAO.findByEmail(email);
            if (user == null) {
                System.out.println("User not found with email: " + email);
                return false;
            }
            
            // Debug logging
            System.out.println("Validating password for user: " + email);
            
            BCrypt.Result result = BCrypt.verifyer().verify(
                password.toCharArray(), 
                user.getPassword().toCharArray()
            );
            
            System.out.println("Password validation result: " + result.verified);
            return result.verified;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error validating credentials: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUserProfile(Long userId) {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user profile: " + e.getMessage(), e);
        }
    }

    @Override
    public User login(String email, String password) {
        try {
            if (!validateCredentials(email, password)) {
                throw new IllegalArgumentException("Invalid email or password");
            }
            return userDAO.findByEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException("Error during login: " + e.getMessage(), e);
        }
    }

    // Helper methods
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private boolean verifyPassword(String inputPassword, String storedHash) {
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(
                inputPassword.toCharArray(), 
                storedHash.toCharArray()
            );
            return result.verified;
        } catch (Exception e) {
            System.out.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
}