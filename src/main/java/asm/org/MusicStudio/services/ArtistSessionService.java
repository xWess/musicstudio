package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.Artist;

public interface ArtistSessionService {
    Artist getCurrentArtist();
    boolean isArtistLoggedIn();
} 