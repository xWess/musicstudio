package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.services.RoomService;
import asm.org.MusicStudio.services.RoomServiceImpl;
import java.time.LocalDate;
import java.time.LocalTime;

public class ArtistRoomBookingServiceImpl implements ArtistRoomBookingService {
    private static ArtistRoomBookingServiceImpl instance;
    private final RoomService roomService;
    private final ArtistSessionService artistSessionService;

    private ArtistRoomBookingServiceImpl() {
        this.roomService = RoomServiceImpl.getInstance();
        this.artistSessionService = ArtistSessionServiceImpl.getInstance();
    }

    public static ArtistRoomBookingServiceImpl getInstance() {
        if (instance == null) {
            instance = new ArtistRoomBookingServiceImpl();
        }
        return instance;
    }

    @Override
    public void bookRoom(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) throws Exception {
        Artist currentArtist = artistSessionService.getCurrentArtist();
        if (currentArtist == null) {
            throw new IllegalStateException("No artist currently logged in");
        }
        roomService.bookRoom(currentArtist, room, date, startTime, endTime);
    }

    @Override
    public boolean isRoomAvailable(Room room, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return room.isAvailable(date, startTime.toString());
    }
} 