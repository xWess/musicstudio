package asm.org.MusicStudio.test;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.UserServiceImpl;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.Student;

public class UserTest {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        User user = new Student(1, "John Doe", "john.doe@example.com", Role.STUDENT.toString());


        try {
            userService.addUser(user);
            System.out.println("User added successfully");
        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());

        }

        User foundUser = userService.findUserByEmail("john.doe@example.com");
        System.out.println("Found user: " + foundUser);

    }
}
