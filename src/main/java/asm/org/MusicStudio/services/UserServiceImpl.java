package asm.org.MusicStudio.services;

import asm.org.MusicStudio.dao.UserDAO;
import asm.org.MusicStudio.entity.User;

import java.sql.SQLException;
import java.util.List;


public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    public void addUser(User user) {
        try {
            userDAO.createUser(user);
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user", e);
        }
    }

    public User findUserByEmail(String email) {
        try {
            return userDAO.findByEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }
    }

    public void updateUser(User user) {
        try {
            userDAO.updateUser(user);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    public void deleteUserById(int userId) {
        try {
            userDAO.deleteUserById(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all users", e);
        }
    }
}
