package asm.org.MusicStudio.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Artist extends User {
    @Builder.Default
    private List<Room> bookedRooms = new ArrayList<>();

    public Artist(Integer id, String name, String email, String role) {
        super(id, name, email, Role.valueOf(role.toUpperCase()));
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