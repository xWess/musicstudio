package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.User;

public class ArtistSessionServiceImpl implements ArtistSessionService {
    private static ArtistSessionServiceImpl instance;
    private final UserService userService;
    private Artist currentArtist;

    private ArtistSessionServiceImpl() {
        this.userService = UserServiceImpl.getInstance();
    }

    public static ArtistSessionServiceImpl getInstance() {
        if (instance == null) {
            instance = new ArtistSessionServiceImpl();
        }
        return instance;
    }

    @Override
    public Artist getCurrentArtist() {
        if (currentArtist == null) {
            User user = userService.getCurrentUser();
            if (user != null && user.getRole() == Role.ARTIST) {
                currentArtist = (Artist) user;
            }
        }
        return currentArtist;
    }

    @Override
    public boolean isArtistLoggedIn() {
        return getCurrentArtist() != null;
    }

    public void setCurrentArtist(Artist artist) {
        this.currentArtist = artist;
    }
} 