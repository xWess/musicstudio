package asm.org.MusicStudio.dao;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class PaymentDAO {
    
    public List<Payment> findByUser(User user) throws SQLException {
        String sql = "SELECT id, description, amount, payment_date, status " +
                    "FROM payments WHERE user_id = ?";
                    
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, user.getId());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = Payment.builder()
                        .id(rs.getLong("id"))
                        .user(user)
                        .description(rs.getString("description"))
                        .amount(rs.getBigDecimal("amount"))
                        .paymentDate(rs.getTimestamp("payment_date").toLocalDateTime())
                        .status(rs.getString("status"))
                        .build();
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching payments for user: " + e.getMessage(), e);
        }
        
        return payments;
    }
    
    public Payment save(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (user_id, description, amount, payment_date, status) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, payment.getUser().getId());
            pstmt.setString(2, payment.getDescription());
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(5, "PENDING");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment.setId(rs.getLong("id"));
                    return payment;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error saving payment: " + e.getMessage(), e);
        }
        
        throw new SQLException("Failed to save payment");
    }

    public Payment findById(long paymentId) throws SQLException {
        String sql = "SELECT p.*, u.name as user_name, u.email as user_email " +
                    "FROM payments p " +
                    "JOIN users u ON p.user_id = u.id " +
                    "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, paymentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new Student(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_email")
                    );
                    
                    return Payment.builder()
                        .id(rs.getLong("id"))
                        .user(user)
                        .description(rs.getString("description"))
                        .amount(rs.getBigDecimal("amount"))
                        .paymentDate(rs.getTimestamp("payment_date").toLocalDateTime())
                        .status(rs.getString("status"))
                        .build();
                }
                return null; // Payment not found
            }
        }
    }

    public List<Payment> findByUserId(int userId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, u.name as user_name, u.email as user_email " +
                    "FROM payments p " +
                    "JOIN users u ON p.user_id = u.id " +
                    "WHERE p.user_id = ? " +
                    "ORDER BY p.payment_date DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new Student(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_email")
                    );
                    
                    Payment payment = Payment.builder()
                        .id(rs.getLong("id"))
                        .user(user)
                        .description(rs.getString("description"))
                        .amount(rs.getBigDecimal("amount"))
                        .paymentDate(rs.getTimestamp("payment_date").toLocalDateTime())
                        .status(rs.getString("status"))
                        .build();
                    
                    payments.add(payment);
                }
            }
        }
        
        return payments;
    }

    public Payment update(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, payment.getStatus());
            pstmt.setLong(2, payment.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating payment failed, no rows affected.");
            }
            
            return payment;
        }
    }

    public Payment saveEnrollmentPayment(Payment payment, LocalDateTime startDate) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Save payment first
            String paymentSql = "INSERT INTO payments (user_id, description, amount, payment_date, status) " +
                               "VALUES (?, ?, ?, ?, ?) RETURNING id";
            
            Long paymentId;
            try (PreparedStatement pstmt = conn.prepareStatement(paymentSql)) {
                pstmt.setInt(1, payment.getUser().getId());
                pstmt.setString(2, payment.getDescription());
                pstmt.setBigDecimal(3, payment.getAmount());
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setString(5, "PENDING");
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create payment record");
                    }
                    paymentId = rs.getLong("id");
                    payment.setId(paymentId);
                }
            }
            
            // Create enrollment with simple query
            String enrollSql = "INSERT INTO enrollments (user_id, course_id, payment_id, start_date) " +
                              "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(enrollSql)) {
                pstmt.setInt(1, payment.getUser().getId());
                pstmt.setInt(2, payment.getCourseId());
                pstmt.setLong(3, paymentId);
                pstmt.setTimestamp(4, Timestamp.valueOf(startDate));
                
                int rows = pstmt.executeUpdate();
                if (rows != 1) {
                    throw new SQLException("Failed to create enrollment record");
                }
            }
            
            conn.commit();
            return payment;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Failed to process enrollment: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
} 