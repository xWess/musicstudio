package asm.org.MusicStudio.services;

import asm.org.MusicStudio.db.DatabaseConnection;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Schedule;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

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
    public List<Room> getAvailableRooms(LocalDate date) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT id, location, capacity FROM rooms ORDER BY location";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Room room = Room.builder()
                    .roomId(rs.getInt("id"))
                    .location(rs.getString("location"))
                    .capacity(rs.getInt("capacity"))
                    .build();
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }
    
    @Override
    public void bookRoom(Artist artist, Room room, LocalDate date, 
            LocalTime startTime, LocalTime endTime) {
        //TODO: Validate room availability using Room.isAvailable()
        //TODO: Create new Schedule entry
        //TODO: Use Artist.bookRoom() method
        //TODO: Use Room.addSchedule() method
    }
    
    @Override
    public void cancelBooking(Artist artist, Room room, Schedule schedule) {
        //TODO: Validate booking ownership
        //TODO: Use Artist.cancelRoomBooking() method
        //TODO: Use Room.removeSchedule() method
        //TODO: Update schedule status
    }
    
    @Override
    public Room getRoomDetails(Long roomId) {
        //TODO: Implement room details retrieval
        //TODO: Include equipment list
        //TODO: Include room features
        //TODO: Include maintenance schedule
        return null;
    }

    @Override
    public List<Room> getAllRooms() throws SQLException {
        return getAvailableRooms(LocalDate.now()); // For now, return all rooms
    }
} 