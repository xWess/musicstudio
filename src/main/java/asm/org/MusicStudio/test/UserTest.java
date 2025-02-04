package asm.org.MusicStudio.test;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.UserServiceImpl;
import asm.org.MusicStudio.entity.Student;

public class UserTest {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        
        // Updated to use the correct constructor
        User user = new Student(1, "John Doe", "john.doe@example.com");
        // Role is automatically set to STUDENT in the Student constructor

        try {
            userService.addUser(user);
            System.out.println("User added successfully");
        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
        }

        User foundUser = userService.findUserByEmail("john.doe@example.com");
        if (foundUser != null) {
            System.out.println("Found user: " + foundUser.getName() + 
                             ", Role: " + foundUser.getRole());
        } else {
            System.out.println("User not found");
        }
    }
}
