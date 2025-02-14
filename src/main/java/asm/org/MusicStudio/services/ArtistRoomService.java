package asm.org.MusicStudio.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.entity.Schedule;

public interface ArtistRoomService {
    List<Room> getAvailableRooms(LocalDate date, LocalTime time) throws SQLException;
    List<Room> getAllRooms(LocalDate date) throws SQLException;
    List<Schedule> getArtistBookings(Artist artist) throws SQLException;
    void cancelBooking(Schedule schedule) throws SQLException;
    Integer bookRoom(Artist artist, Room room, LocalDate date, LocalTime startTime, LocalTime endTime) throws SQLException;
} 