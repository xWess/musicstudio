package asm.org.MusicStudio.entity;

import java.util.ArrayList;
import java.util.List;

public class Artist extends User {
    private List<Room> bookedRooms = new ArrayList<>();

    public Artist() {
        super();
        setRole(Role.ARTIST);
    }

    public Artist(Integer id, String name, String email) {
        super();
        setId(id);
        setName(name);
        setEmail(email);
        setRole(Role.ARTIST);
        this.bookedRooms = new ArrayList<>();
    }

    // Helper methods
    public void bookRoom(Room room) {
        if (room != null && !bookedRooms.contains(room)) {
            bookedRooms.add(room);
        }
    }

    public void cancelRoomBooking(Room room) {
        bookedRooms.remove(room);
    }
}