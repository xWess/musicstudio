package asm.org.MusicStudio.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;

public class RoomServiceImpl implements RoomService {
    
    private static RoomServiceImpl instance;
    
    public RoomServiceImpl() {}
    
    public static RoomServiceImpl getInstance() {
        if (instance == null) {
            instance = new RoomServiceImpl();
        }
        return instance;
    }
    
    @Override
    public List<Room> getAvailableRooms(LocalDate date, LocalTime time) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.*, 
                   COALESCE(r.price, 50.00) as room_price  -- Use default price if null
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
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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
                room.setPrice(rs.getBigDecimal("room_price")); // Will never be null now
                rooms.add(room);
            }
        }
        return rooms;
    }
    
    @Override
    public void bookRoom(Artist artist, Room room, LocalDate date, 
            LocalTime startTime, LocalTime endTime) {
        try {
            String sql = """
                INSERT INTO bookings (room_id, user_id, booking_date, start_time, end_time, status) 
                VALUES (?, ?, ?, ?, ?, 'BOOKED')
                """;
            
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, room.getRoomId());
                stmt.setInt(2, artist.getId());
                stmt.setDate(3, java.sql.Date.valueOf(date));
                stmt.setTime(4, java.sql.Time.valueOf(startTime));
                stmt.setTime(5, java.sql.Time.valueOf(endTime));
                
                stmt.executeUpdate();
                
                // Update room status
                updateRoomStatus(room.getRoomId(), "BOOKED");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error booking room: " + e.getMessage(), e);
        }
    }
    
    private void updateRoomStatus(int roomId, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        }
    }
    
    @Override
    public void cancelBooking(Artist artist, Room room, Schedule schedule) {
        try {
            // Validate booking ownership
            String validateSql = "SELECT COUNT(*) FROM schedules WHERE id = ? AND booked_by = ? AND room_id = ?";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(validateSql)) {
                
                stmt.setInt(1, schedule.getScheduleId());
                stmt.setInt(2, artist.getId());
                stmt.setInt(3, room.getRoomId());
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    throw new IllegalStateException("Booking not found or unauthorized cancellation");
                }
            }

            // Update schedule status
            String updateSql = "UPDATE schedules SET status = 'CANCELLED' WHERE id = ?";
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                
                stmt.setInt(1, schedule.getScheduleId());
                stmt.executeUpdate();
                
                // Update domain objects
                room.removeSchedule(schedule);
                artist.cancelRoomBooking(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cancelling booking: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Room getRoomDetails(Long roomId) {
        String sql = """
            SELECT r.*, 
                   r.price as room_price,
                   e.name as equipment_name, 
                   e.description as equipment_desc,
                   m.date as maintenance_date, 
                   m.description as maintenance_desc
            FROM rooms r
            LEFT JOIN room_equipment re ON r.id = re.room_id
            LEFT JOIN equipment e ON re.equipment_id = e.id
            LEFT JOIN maintenance m ON r.id = m.room_id
            WHERE r.id = ?
            """;
            
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roomId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Room room = Room.builder()
                    .roomId(rs.getInt("id"))
                    .location(rs.getString("location"))
                    .capacity(rs.getInt("capacity"))
                    .type(rs.getString("type"))
                    .price(rs.getBigDecimal("room_price"))
                    .build();
                
                // Add equipment and maintenance details
                while (!rs.isAfterLast()) {
                    if (rs.getString("equipment_name") != null) {
                        room.addEquipment(rs.getString("equipment_name"), 
                                        rs.getString("equipment_desc"));
                    }
                    if (rs.getDate("maintenance_date") != null) {
                        room.addMaintenance(rs.getDate("maintenance_date").toLocalDate(),
                                          rs.getString("maintenance_desc"));
                    }
                    rs.next();
                }
                return room;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching room details: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.*, 
                   r.price as room_price,  -- Add price to the query
                   COUNT(s.id) as booking_count
            FROM rooms r
            LEFT JOIN schedules s ON r.id = s.room_id AND s.status = 'BOOKED'
            GROUP BY r.id
            ORDER BY r.location
            """;
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Room room = Room.builder()
                    .roomId(rs.getInt("id"))
                    .location(rs.getString("location"))
                    .capacity(rs.getInt("capacity"))
                    .type(rs.getString("type"))
                    .price(rs.getBigDecimal("room_price")) // Set the price
                    .build();
                rooms.add(room);
            }
        }
        return rooms;
    }
} 