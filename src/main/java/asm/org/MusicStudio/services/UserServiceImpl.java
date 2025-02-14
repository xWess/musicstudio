package asm.org.MusicStudio.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import asm.org.MusicStudio.dao.UserDAO;
import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Teacher;
import asm.org.MusicStudio.entity.User;
import at.favre.lib.crypto.bcrypt.BCrypt;


public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private static UserServiceImpl instance;
    private User currentUser;
    
    public static UserServiceImpl getInstance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    @Override
    public void addUser(User user) {
        try {
            // For new users
            if (user.getId() == 0) {
                if (findUserByEmail(user.getEmail()) != null) {
                    throw new IllegalArgumentException("Email already exists");
                }
            }
            
            // Create appropriate user type
            User newUser;
            if (user.getRole() == Role.STUDENT) {
                Student student = new Student();
                student.setId(user.getId());
                student.setName(user.getName());
                student.setEmail(user.getEmail());
                student.setRole(user.getRole());
                student.setPassword(user.getPassword());
                student.setSalt(user.getSalt());
                student.setActive(user.isActive());
                newUser = student;
            } else if (user.getRole() == Role.TEACHER) {
                Teacher teacher = new Teacher();
                teacher.setId(user.getId());
                teacher.setName(user.getName());
                teacher.setEmail(user.getEmail());
                teacher.setRole(user.getRole());
                teacher.setPassword(user.getPassword());
                teacher.setSalt(user.getSalt());
                teacher.setActive(user.isActive());
                newUser = teacher;
            } else {
                throw new IllegalArgumentException("Unsupported user role: " + user.getRole());
            }
            
            userDAO.createUser(newUser);
            
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user", e);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        try {
            return userDAO.findByEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
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
                    String roleStr = rs.getString("role");
                    Role role = Role.fromString(roleStr);
                    
                    User user = createUserByRole(role);
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(role);
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

    @Override
    public List<Teacher> getAllTeachers() {
    try {
        return userDAO.findByRole(Role.TEACHER)
            .stream()
            .map(user -> new Teacher(user.getId(), user.getName(), user.getEmail()))
            .collect(Collectors.toList());
    } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public int getTeacherIdByName(String instructor) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTeacherIdByName'");
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private User createUserByRole(Role role) {
        switch (role) {
            case STUDENT:
                return new Student();
            case TEACHER:
                return new Teacher();
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }
    }
}
