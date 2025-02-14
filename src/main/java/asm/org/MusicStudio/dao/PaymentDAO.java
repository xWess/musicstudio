package asm.org.MusicStudio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Payment;

public class PaymentDAO {
    
    public List<Payment> findByUserId(int userId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = """
            SELECT p.*, c.name as course_name 
            FROM payments p
            LEFT JOIN courses c ON p.course_id = c.id
            WHERE p.user_id = ?
            ORDER BY p.payment_date DESC
            """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(buildPaymentFromResultSet(rs));
                }
            }
        }
        return payments;
    }
    
    public Payment save(Payment payment) throws SQLException {
        String sql = """
            INSERT INTO payments (user_id, description, amount, payment_date, status, 
                                payment_method, course_id, room_booking_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id
            """;
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, payment.getUserId());
            pstmt.setString(2, payment.getDescription());
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate()));
            pstmt.setString(5, payment.getStatus());
            pstmt.setString(6, payment.getPaymentMethod());
            
            // Handle nullable course_id
            if (payment.getCourseId() != null) {
                pstmt.setInt(7, payment.getCourseId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            
            // Handle nullable room_booking_id
            if (payment.getRoomBookingId() != null) {
                pstmt.setInt(8, payment.getRoomBookingId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment.setId(rs.getLong("id"));
                    return payment;
                }
            }
        }
        throw new SQLException("Failed to save payment");
    }

    public Payment findById(long paymentId) throws SQLException {
        String sql = """
            SELECT p.*, c.name as course_name 
            FROM payments p
            LEFT JOIN courses c ON p.course_id = c.id
            WHERE p.id = ?
            """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, paymentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildPaymentFromResultSet(rs);
                }
                return null;
            }
        }
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

    private Payment buildPaymentFromResultSet(ResultSet rs) throws SQLException {
        return Payment.builder()
            .id(rs.getLong("id"))
            .userId(rs.getInt("user_id"))
            .courseId(getIntOrNull(rs, "course_id"))
            .amount(rs.getBigDecimal("amount"))
            .paymentDate(rs.getTimestamp("payment_date").toLocalDateTime())
            .status(rs.getString("status"))
            .paymentMethod(rs.getString("payment_method"))
            .roomBookingId(getIntOrNull(rs, "room_booking_id"))
            .description(rs.getString("description"))
            .build();
    }

    private void setNullableInt(PreparedStatement pstmt, int parameterIndex, Integer value) throws SQLException {
        if (value != null) {
            pstmt.setInt(parameterIndex, value);
        } else {
            pstmt.setNull(parameterIndex, java.sql.Types.INTEGER);
        }
    }

    private Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    public Payment processEnrollmentPayment(Payment payment, LocalDate startDate) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // First insert payment
            String paymentSql = """
                INSERT INTO payments (user_id, amount, payment_date, status, description, course_id) 
                VALUES (?, ?, ?, ?, ?, ?) 
                RETURNING id
                """;
            
            try (PreparedStatement pstmt = conn.prepareStatement(paymentSql)) {
                pstmt.setInt(1, payment.getUserId());
                pstmt.setBigDecimal(2, payment.getAmount());
                pstmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
                pstmt.setString(4, payment.getStatus());
                pstmt.setString(5, payment.getDescription());
                pstmt.setInt(6, payment.getCourseId());
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to create payment record");
                    }
                    payment.setId(rs.getLong("id"));
                }
            }
            
            // Then create enrollment
            String enrollSql = "INSERT INTO enrollments (student_id, course_id, payment_id, start_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(enrollSql)) {
                pstmt.setInt(1, payment.getUserId());
                pstmt.setInt(2, payment.getCourseId());
                pstmt.setLong(3, payment.getId());
                pstmt.setDate(4, java.sql.Date.valueOf(startDate));
                
                int rows = pstmt.executeUpdate();
                if (rows != 1) {
                    throw new SQLException("Failed to create enrollment record");
                }
            }
            
            conn.commit();
            return payment;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException("Failed to process enrollment: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    public Payment saveEnrollmentPayment(Payment payment, LocalDateTime startDate) throws SQLException {
        String sql = "INSERT INTO payments (user_id, description, amount, payment_date, status) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id";
                    
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getUserId());
            pstmt.setString(2, payment.getDescription());
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setTimestamp(4, Timestamp.valueOf(startDate));
            pstmt.setString(5, "PENDING");  
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    payment.setId(rs.getLong("id"));
                    return payment;
                }
            }
        }
        throw new SQLException("Failed to save enrollment payment");
    }   

    public List<Payment> findByUserIdAndStatus(Integer userId, String status) throws SQLException {
        String sql = """
            SELECT p.*, c.name as course_name 
            FROM payments p
            JOIN courses c ON p.course_id = c.id
            WHERE p.student_id = ? AND p.status = ?
            ORDER BY p.payment_date DESC
        """;
        
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = Payment.builder()
                        .id(rs.getLong("id"))
                        .courseId(rs.getInt("course_id"))
                        .amount(rs.getBigDecimal("amount"))
                        .paymentDate(rs.getTimestamp("payment_date").toLocalDateTime())
                        .status(rs.getString("status"))
                        .description(rs.getString("description"))
                        .build();
                    payments.add(payment);
                }
            }
        }
        return payments;
    }
} 