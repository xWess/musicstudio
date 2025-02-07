package asm.org.MusicStudio.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import asm.org.MusicStudio.entity.Room;

public interface RoomService {
    
    /**
     * Gets available rooms for a specific date
     * @param date The date to check
     * @return List of available rooms
     */
    List<Room> getAvailableRooms(LocalDate date);
    
    /**
     * Books a room for a user
     * @param roomId The room ID
     * @param userId The user ID
     * @param date The booking date
     * @param startTime Start time
     * @param endTime End time
     */
    void bookRoom(int roomId, int userId, LocalDate date, 
            LocalTime startTime, LocalTime endTime);
    
    /**
     * Cancels a booking
     * @param bookingId The booking ID
     */
    void cancelBooking(int bookingId);
    
    /**
     * Checks if a room is available for booking
     * @param roomId The room ID
     * @param date The booking date
     * @param startTime Start time
     * @param endTime End time
     * @return true if room is available, false otherwise
     */
    boolean isRoomAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    /**
     * Gets room details
     * @param roomId The room ID
     * @return Room details
     */
    Room getRoomDetails(Long roomId);
    
    /**
     * Gets booking history for a user
     * @param userId The user ID
     * @return List of room bookings
     */
    List<Room> getRoomBookingHistory(int userId);
} 