package asm.org.MusicStudio.test;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordTest {
    public static void main(String[] args) {
        // 1. Test direct verification with stored hash
        String storedHash = "$2a$12$8tWMZ8fwC6UHcxmFjLXgxeQYvHDcvZRXE7Ub7RBpG7.HgQzpXnNDu";
        String password = "admin123";
        
        System.out.println("Test 1 - Direct verification:");
        BCrypt.Result result1 = BCrypt.verifyer().verify(
            password.toCharArray(), 
            storedHash.toCharArray()
        );
        System.out.println("Result 1: " + result1.verified);

        // 2. Generate new hash and verify
        System.out.println("\nTest 2 - New hash generation and verification:");
        String newHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        System.out.println("New hash: " + newHash);
        
        BCrypt.Result result2 = BCrypt.verifyer().verify(
            password.toCharArray(), 
            newHash.toCharArray()
        );
        System.out.println("Result 2: " + result2.verified);
        
        // 3. Test with raw bytes
        System.out.println("\nTest 3 - Raw byte verification:");
        byte[] hashedBytes = BCrypt.withDefaults().hash(12, password.toCharArray());
        String hashFromBytes = new String(hashedBytes);
        System.out.println("Hash from bytes: " + hashFromBytes);
        
        BCrypt.Result result3 = BCrypt.verifyer().verify(
            password.toCharArray(), 
            hashedBytes
        );
        System.out.println("Result 3: " + result3.verified);
    }
} 