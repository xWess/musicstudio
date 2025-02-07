package asm.org.MusicStudio.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import asm.org.MusicStudio.dao.RoomDAO;
import asm.org.MusicStudio.entity.Room;

public class RoomServiceImpl implements RoomService {
    private final RoomDAO roomDAO;

    public RoomServiceImpl() {
        this.roomDAO = new RoomDAO();
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate date) {
        try {
            return roomDAO.findAvailableRooms(date);
        } catch (Exception e) {
            throw new RuntimeException("Error getting available rooms: " + e.getMessage(), e);
        }
    }

    @Override
    public void bookRoom(int roomId, int userId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        try {
            if (!isRoomAvailable(roomId, date, startTime, endTime)) {
                throw new RuntimeException("Room is not available for the selected time slot");
            }
            roomDAO.bookRoom(roomId, userId, date, startTime, endTime);
        } catch (Exception e) {
            throw new RuntimeException("Error booking room: " + e.getMessage(), e);
        }
    }

    @Override
    public void cancelBooking(int bookingId) {
        try {
            roomDAO.cancelBooking(bookingId);
        } catch (Exception e) {
            throw new RuntimeException("Error canceling booking: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        try {
            return roomDAO.isRoomAvailable(roomId, date, startTime, endTime);
        } catch (Exception e) {
            throw new RuntimeException("Error checking room availability: " + e.getMessage(), e);
        }
    }

    @Override
    public Room getRoomDetails(Long roomId) {
        try {
            return roomDAO.findById(roomId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting room details: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Room> getRoomBookingHistory(int userId) {
        try {
            return roomDAO.getRoomBookingHistory(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting booking history: " + e.getMessage(), e);
        }
    }
} 