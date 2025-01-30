package asm.org.MusicStudio.entity;

public enum Role {
    STUDENT,
    TEACHER,
    ARTIST;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
} 