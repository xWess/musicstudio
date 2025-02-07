package asm.org.MusicStudio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Room;

public class RoomDAO {
    
    public List<Room> findAvailableRooms(LocalDate date) {
        String sql = """
            SELECT r.id, r.location, r.capacity, 
                   CASE WHEN EXISTS (
                       SELECT 1 FROM bookings b 
                       WHERE b.room_id = r.id 
                       AND b.booking_date = ?
                       AND b.start_time::time >= '09:00'::time 
                       AND b.end_time::time <= '18:00'::time
                   ) THEN 'Booked' ELSE 'Available' END as status
            FROM rooms r
            GROUP BY r.id, r.location, r.capacity
            ORDER BY r.location
            """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            List<Room> rooms = new ArrayList<>();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setLocation(rs.getString("location"));
                room.setCapacity(rs.getInt("capacity"));
                room.updateAvailabilityProperty("Booked".equals(rs.getString("status")));
                rooms.add(room);
            }
            return rooms;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available rooms: " + e.getMessage(), e);
        }
    }

    public void bookRoom(int roomId, int userId, LocalDate date, 
            LocalTime startTime, LocalTime endTime) {
        String sql = "INSERT INTO bookings (room_id, user_id, booking_date, start_time, end_time) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setInt(2, userId);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setTime(4, java.sql.Time.valueOf(startTime));
            stmt.setTime(5, java.sql.Time.valueOf(endTime));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to book room: " + e.getMessage(), e);
        }
    }

    public void cancelBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            int result = stmt.executeUpdate();
            if (result != 1) {
                throw new RuntimeException("Failed to cancel booking");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to cancel booking: " + e.getMessage(), e);
        }
    }

    public Room findById(Long roomId) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roomId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setLocation(rs.getString("location"));
                room.setCapacity(rs.getInt("capacity"));
                return room;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find room: " + e.getMessage(), e);
        }
    }

    public List<Room> getRoomBookingHistory(int userId) {
        String sql = """
            SELECT r.id, r.location, r.capacity, b.booking_date, 
                   b.start_time, b.end_time
            FROM rooms r
            JOIN bookings b ON r.id = b.room_id
            WHERE b.user_id = ?
            ORDER BY b.booking_date DESC, b.start_time DESC
            """;
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            List<Room> bookings = new ArrayList<>();
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setLocation(rs.getString("location"));
                room.setCapacity(rs.getInt("capacity"));
                room.setDate(rs.getDate("booking_date").toLocalDate());
                room.setTimeSlot(String.format("%s - %s", 
                    rs.getTime("start_time").toString(),
                    rs.getTime("end_time").toString()));
                bookings.add(room);
            }
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get booking history: " + e.getMessage(), e);
        }
    }

    public boolean isRoomAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM bookings 
            WHERE room_id = ? AND booking_date = ? 
            AND ((start_time <= ? AND end_time > ?) 
            OR (start_time < ? AND end_time >= ?)
            OR (start_time >= ? AND end_time <= ?))
            """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.setTime(3, java.sql.Time.valueOf(startTime));
            stmt.setTime(4, java.sql.Time.valueOf(startTime));
            stmt.setTime(5, java.sql.Time.valueOf(endTime));
            stmt.setTime(6, java.sql.Time.valueOf(endTime));
            stmt.setTime(7, java.sql.Time.valueOf(startTime));
            stmt.setTime(8, java.sql.Time.valueOf(endTime));
            
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check room availability: " + e.getMessage(), e);
        }
    }

    public boolean isTimeSlotAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String sql = """
            SELECT COUNT(*) FROM bookings 
            WHERE room_id = ? 
            AND booking_date = ?
            AND ((start_time <= ? AND end_time > ?) 
            OR (start_time < ? AND end_time >= ?)
            OR (start_time >= ? AND end_time <= ?))
            """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.setTime(3, java.sql.Time.valueOf(startTime));
            stmt.setTime(4, java.sql.Time.valueOf(startTime));
            stmt.setTime(5, java.sql.Time.valueOf(endTime));
            stmt.setTime(6, java.sql.Time.valueOf(endTime));
            stmt.setTime(7, java.sql.Time.valueOf(startTime));
            stmt.setTime(8, java.sql.Time.valueOf(endTime));
            
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check time slot availability: " + e.getMessage(), e);
        }
    }
}