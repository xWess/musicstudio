package asm.org.MusicStudio.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static void main(String[] args) {
        // Use this to generate a hash for "admin123"
        String hash = hashPassword("admin123");
        System.out.println("Hash for admin123: " + hash);
        
        // Verify the hash
        boolean verified = BCrypt.verifyer().verify(
            "admin123".toCharArray(), 
            hash.toCharArray()
        ).verified;
        System.out.println("Verification test: " + verified);
    }
} 