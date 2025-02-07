package asm.org.MusicStudio.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Artist extends User {
    @Builder.Default
    private List<Room> bookedRooms = new ArrayList<>();

    public Artist() {
        super();
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