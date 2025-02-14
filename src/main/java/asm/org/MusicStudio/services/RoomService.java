package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public interface RoomService {
    
    /**
     * Gets available rooms for a specific date and time
     * @param date The date to check
     * @param time The time to check
     * @return List of available rooms
     */
    List<Room> getAvailableRooms(LocalDate date, LocalTime time) throws SQLException;
    
    /**
     * Gets available rooms for a specific date (at any time)
     * @param date The date to check
     * @return List of available rooms
     */
    default List<Room> getAvailableRooms(LocalDate date) throws SQLException {
        return getAvailableRooms(date, LocalTime.of(9, 0)); // Default to 9 AM
    }
    
    /**
     * Gets all rooms regardless of availability
     * @return List of all rooms
     */
    List<Room> getAllRooms() throws SQLException;
    
    /**
     * Books a room for an artist
     * @param artist The booking artist
     * @param room The room to book
     * @param date The booking date
     * @param startTime Start time
     * @param endTime End time
     */
    void bookRoom(Artist artist, Room room, LocalDate date, 
            LocalTime startTime, LocalTime endTime);
    
    /**
     * Cancels a room booking
     * @param artist The artist who booked
     * @param room The booked room
     * @param schedule The schedule to cancel
     */
    void cancelBooking(Artist artist, Room room, Schedule schedule);
    
    /**
     * Gets room details including equipment
     * @param roomId The room ID
     * @return Room details
     */
    Room getRoomDetails(Long roomId);

    static RoomService getInstance() {
        return RoomServiceImpl.getInstance();
    }
} 