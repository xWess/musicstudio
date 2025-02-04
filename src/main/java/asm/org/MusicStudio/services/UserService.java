package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;

import java.util.List;
import java.time.LocalDateTime;
import java.sql.SQLException;

public interface UserService {
    void addUser(User user);

    User findUserByEmail(String email);

    void deleteUserById(int userId);

    List<User> getAllUsers();

    User findUserByResetToken(String resetToken);

    List<User> getUsersByRole(Role role);

    boolean isAuthorized(User user, Role requiredRole);

    /**
     * Updates user password
     * 
     * @param userId      The user ID
     * @param newPassword The new password
     */
    void updatePassword(Long userId, String newPassword);

    /**
     * Gets current user profile
     * 
     * @param userId The user ID
     * @return User profile
     */
    User getUserProfile(Long userId);

    /**
     * Validates user credentials
     * 
     * @param email    User email
     * @param password User password
     * @return boolean indicating valid credentials
     */
    boolean validateCredentials(String email, String password);

    /**
     * Updates user profile information
     * 
     * @param user The user to update
     */
    void updateUser(User user);

    /**
     * Updates user password
     * 
     * @param user            The user
     * @param currentPassword The current password for verification
     * @param newPassword     The new password
     */
    void updatePassword(User user, String currentPassword, String newPassword) throws SQLException;

    /**
     * Handles user login
     * 
     * @param email    User email
     * @param password User password
     * @return The logged in user
     */
    User login(String email, String password);

    /**
     * Initiates password reset
     * 
     * @param email User email
     */
    void initiatePasswordReset(String email);
}