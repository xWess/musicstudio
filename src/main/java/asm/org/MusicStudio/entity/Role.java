package asm.org.MusicStudio.entity;

public enum Role {
    STUDENT,
    TEACHER,
    ARTIST,
    ADMIN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static Role fromString(String roleStr) {
        try {
            return valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleStr);
        }
    }
} 