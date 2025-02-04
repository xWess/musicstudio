package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Schedule;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RoomService {
    
    /**
     * Gets available rooms for a specific date
     * @param date The date to check
     * @return List of available rooms
     */
    List<Room> getAvailableRooms(LocalDate date);
    
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
} 