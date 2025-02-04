package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.exception.AuthenticationException;
import at.favre.lib.crypto.bcrypt.BCrypt;
import java.time.LocalDateTime;

public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User register(User user, String password) {
        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        user.setPassword(hashedPassword);
        user.setActive(true);
        userService.addUser(user);
        return user;
    }

    public User login(String email, String password) throws AuthenticationException {
        User user = userService.findUserByEmail(email);
        
        if (user == null || !user.isActive()) {
            System.out.println("User not found or inactive: " + email); // Debug
            throw new AuthenticationException("Invalid credentials");
        }
        
        // Debug prints
        System.out.println("Attempting login for: " + email);
        System.out.println("Stored password hash: " + user.getPassword());
        System.out.println("Provided password: " + password);
        
        BCrypt.Result result = BCrypt.verifyer().verify(
            password.toCharArray(), 
            user.getPassword().toCharArray()
        );
        
        System.out.println("BCrypt verification result: " + result.verified); // Debug
        
        if (!result.verified) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        user.setLastLogin(LocalDateTime.now());
        userService.updateUser(user);
        
        return user;
    }

    // Static factory method for convenience
    public static AuthService createDefault() {
        return new AuthService(new UserServiceImpl());
    }
} 