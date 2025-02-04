package asm.org.MusicStudio.services;

import asm.org.MusicStudio.dao.PaymentDAO;
import asm.org.MusicStudio.dao.EnrollmentDAO;
import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Enrollment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
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

    public Payment createPayment(Payment payment) throws SQLException {
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");
        return paymentDAO.save(payment);
    }

    public List<Payment> getUserPayments(User user) throws SQLException {
        return paymentDAO.findByUserId(user.getId());
    }

    public Payment updatePaymentStatus(Long paymentId, String status) throws SQLException {
        Payment payment = paymentDAO.findById(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found with id: " + paymentId);
        }
        payment.setStatus(status);
        return paymentDAO.update(payment);
    }

    public Payment processEnrollmentPayment(User user, Course course, int months) throws SQLException {
        BigDecimal monthlyFee = BigDecimal.valueOf(course.getMonthlyFee());
        BigDecimal totalAmount = monthlyFee.multiply(BigDecimal.valueOf(months));
        LocalDateTime startDate = LocalDateTime.now();
        
        Payment payment = Payment.builder()
            .user(user)
            .description(String.format("Enrollment in %s for %d months", course.getName(), months))
            .amount(totalAmount)
            .courseId(course.getId())
            .build();
        
        return paymentDAO.saveEnrollmentPayment(payment, startDate);
    }

    public List<Course> getAvailableCourses() throws SQLException {
        return courseDAO.findAllActiveCourses();
    }
} 