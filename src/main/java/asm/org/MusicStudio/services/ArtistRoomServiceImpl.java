package asm.org.MusicStudio.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;

public class ArtistRoomServiceImpl implements ArtistRoomService {
    private final DatabaseConnection dbConnection;

    public ArtistRoomServiceImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Integer bookRoom(Artist artist, Room room, LocalDate date, LocalTime startTime, LocalTime endTime) throws SQLException {
        String sql = """
            INSERT INTO bookings (room_id, user_id, booking_date, start_time, end_time) 
            VALUES (?, ?, ?, ?, ?)
            RETURNING id
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, room.getRoomId());
            stmt.setInt(2, artist.getId());
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setTime(4, java.sql.Time.valueOf(startTime));
            stmt.setTime(5, java.sql.Time.valueOf(endTime));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Failed to get booking ID");
        }
    }

    @Override
    public void cancelBooking(Schedule schedule) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED'::booking_status WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, schedule.getScheduleId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Schedule> getArtistBookings(Artist artist) throws SQLException {
        List<Schedule> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date, start_time";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, artist.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setScheduleId(rs.getInt("id"));
                schedule.setDate(rs.getDate("booking_date").toLocalDate());
                
                LocalDate date = rs.getDate("booking_date").toLocalDate();
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                
                schedule.setTimeRange(
                    LocalDateTime.of(date, startTime),
                    LocalDateTime.of(date, endTime)
                );
                
                schedule.setStatus(rs.getString("status"));
                bookings.add(schedule);
            }
        }
        return bookings;
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate date, LocalTime time) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.* 
            FROM rooms r 
            WHERE NOT EXISTS (
                SELECT 1 FROM bookings b 
                WHERE b.room_id = r.id 
                AND b.booking_date = ? 
                AND b.start_time = ?
                AND b.status = 'BOOKED'
            )
            ORDER BY r.location
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setTime(2, java.sql.Time.valueOf(time));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("id"));
                room.setLocation(rs.getString("location"));
                room.setCapacity(rs.getInt("capacity"));
                room.setType(rs.getString("type"));
                rooms.add(room);
            }
        }
        return rooms;
    }

    @Override
    public List<Room> getAllRooms(LocalDate date) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.*, 
                   b.user_id as booked_by,
                   u.name as booked_by_name,
                   b.start_time as booking_time,
                   CASE WHEN b.booking_date = ? 
                        THEN r.status::text
                        ELSE 'AVAILABLE' 
                   END as availability
            FROM rooms r
            LEFT JOIN bookings b ON r.id = b.room_id 
                AND b.booking_date = ?
            LEFT JOIN users u ON b.user_id = u.id
            ORDER BY r.location
            """;
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("id"));
                room.setLocation(rs.getString("location"));
                room.setCapacity(rs.getInt("capacity"));
                room.setType(rs.getString("type"));
                room.setAvailability(rs.getString("availability"));
                room.setBookedByName(rs.getString("booked_by_name"));
                
                Time bookingTime = rs.getTime("booking_time");
                if (bookingTime != null) {
                    room.setBookingTime(bookingTime.toLocalTime().toString());
                }
                
                rooms.add(room);
            }
        }
        return rooms;
    }
}