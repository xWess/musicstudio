package asm.org.MusicStudio.security;

public class SecurityContext {
    private static String currentUsername;
    
    public static String getCurrentUsername() {
        return currentUsername;
    }
    
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
} 