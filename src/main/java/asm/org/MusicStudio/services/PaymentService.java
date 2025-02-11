package asm.org.MusicStudio.services;

import asm.org.MusicStudio.dao.PaymentDAO;
import asm.org.MusicStudio.dao.EnrollmentDAO;
import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Course;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.courseDAO = new CourseDAO();
    }

    public Payment processEnrollmentPayment(User user, Course course, int months) throws SQLException {
        BigDecimal totalAmount = BigDecimal.valueOf(course.getMonthlyFee()).multiply(BigDecimal.valueOf(months));
        
        Payment payment = Payment.builder()
            .user(user)
            .courseId(course.getId())
            .description(String.format("Enrollment in %s for %d months", course.getName(), months))
            .amount(totalAmount)
            .paymentDate(LocalDateTime.now())
            .status("COMPLETED")  // Changed from PENDING since we're not using Stripe
            .build();
        
        return paymentDAO.save(payment);
    }

    public List<Payment> getUserPayments(User user) throws SQLException {
        return paymentDAO.findByUserId(user.getId());
    }

    public List<Course> getAvailableCourses() throws SQLException {
        return courseDAO.findAllActiveCourses();
    }
} 