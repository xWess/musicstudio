package asm.org.MusicStudio.test;

import asm.org.MusicStudio.entity.*;
import asm.org.MusicStudio.services.*;
import java.util.List;
import java.time.LocalDate;

public class Student_EnrollmentTest {
    public static void main(String[] args) {
        StudentService studentService = new StudentServiceImpl();
        UserServiceImpl userService = UserServiceImpl.getInstance();
        EnrollmentService enrollmentService = EnrollmentServiceImpl.getInstance();
        PaymentService paymentService = new PaymentService();
        
        // Create and save test student
        Student student = new Student(80, "etudiant name", "etudiant@gmail.com");
        
        try {
            // First save the student
            userService.addUser(student);
            System.out.println("Test student created successfully");
            
            // Test 1: Get available courses
            System.out.println("Test 1: Getting available courses");
            List<Course> availableCourses = studentService.getAvailableCourses();
            System.out.println("Available courses: " + availableCourses.size());
            
            // Test 2: Get current enrollments
            System.out.println("\nTest 2: Getting current enrollments");
            List<Enrollment> currentEnrollments = studentService.getCurrentEnrollments(student);
            System.out.println("Current enrollments: " + currentEnrollments.size());
            
            // Test 3: Get enrollment history
            System.out.println("\nTest 3: Getting enrollment history");
            List<Enrollment> enrollmentHistory = studentService.getEnrollmentHistory(student);
            System.out.println("Enrollment history: " + enrollmentHistory.size());
            
            // Test 4: Check enrollment eligibility
            if (!availableCourses.isEmpty()) {
                Course firstCourse = availableCourses.get(0);
                System.out.println("\nTest 4: Checking if can enroll in course: " + firstCourse.getName());
                boolean canEnroll = studentService.canEnrollInCourse(student, firstCourse);
                System.out.println("Can enroll: " + canEnroll);

                // If student can enroll, test the enrollment process
                if (canEnroll) {
                    System.out.println("\nTest 4.1: Testing payment and enrollment process");
                    
                    try {
                        // Test payment creation
                        System.out.println("Creating payment for 3 months...");
                        Payment payment = paymentService.processEnrollmentPayment(student, firstCourse, 3);
                        System.out.println("Payment created with ID: " + payment.getId());
                        System.out.println("Payment amount: $" + payment.getAmount());
                        System.out.println("Payment status: " + payment.getStatus());
                        
                        // Test payment retrieval
                        System.out.println("\nChecking student's payments...");
                        List<Payment> studentPayments = paymentService.getUserPayments(student);
                        System.out.println("Total payments found: " + studentPayments.size());
                        
                        // Create enrollment with payment
                        System.out.println("\nCreating enrollment with payment...");
                        enrollmentService.createEnrollment(student, firstCourse, LocalDate.now(), payment);
                        System.out.println("Enrollment created successfully!");
                        
                    } catch (Exception e) {
                        System.out.println("Payment/Enrollment process failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            // Test 5: Get student profile
            System.out.println("\nTest 5: Getting student profile");
            Student profile = studentService.getStudentProfile(student.getId());
            if (profile != null) {
                System.out.println("Found profile for: " + profile.getName());
            } else {
                System.out.println("Profile not found");
            }
            
        } catch (Exception e) {
            System.out.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 