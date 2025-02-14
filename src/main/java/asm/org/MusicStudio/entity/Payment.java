package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long id;
    private Integer userId;  // Changed from User to userId to match DB
    private String description;    // e.g., "Guitar Course - January", "Room Booking - Studio A"
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;         // "COMPLETED", "PENDING", etc.
    private Integer courseId;
    private String paymentMethod;  // Added for payment method
    private Integer roomBookingId; // Added for room bookings

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getRoomBookingId() {
        return roomBookingId;
    }

    public void setRoomBookingId(Integer roomBookingId) {
        this.roomBookingId = roomBookingId;
    }
} 