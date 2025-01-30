package asm.org.MusicStudio.services;

import asm.org.MusicStudio.dao.UserDAO;
import asm.org.MusicStudio.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    void addUser(User user);

    User findUserByEmail(String email);

    void updateUser(User user);

    void deleteUserById(int userId);

    List<User> getAllUsers();
}