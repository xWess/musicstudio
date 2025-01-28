package asm.org.MusicStudio.entity;

import java.util.ArrayList;
import java.util.List;

public class Artist {
    private Integer artistId;
    private String name;
    private List<Room> rooms = new ArrayList<>();

    // Default constructor
    public Artist() {}

    // Parameterized constructor
    public Artist(Integer artistId, String name) {
        this.artistId = artistId;
        this.name = name;
    }

    // Getters and setters
    public Integer getArtistId() { return artistId; }
    public void setArtistId(Integer artistId) { this.artistId = artistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }

    // Helper methods
    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
    }
} 