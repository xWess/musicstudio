package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import java.time.LocalDate;
import java.time.LocalTime;

public interface ArtistRoomBookingService {
    void bookRoom(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) throws Exception;
    boolean isRoomAvailable(Room room, LocalDate date, LocalTime startTime, LocalTime endTime);
} 