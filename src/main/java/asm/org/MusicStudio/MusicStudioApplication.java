package asm.org.MusicStudio;

import asm.org.MusicStudio.db.DatabaseConnection;

public class MusicStudioApplication {
    public static void main(String[] args) {
        System.out.println("Music Studio Application Started");
        
        // Test database connection
        try {
            DatabaseConnection.getInstance().getConnection();
            System.out.println("Successfully connected to the database");
        } catch (Exception e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
        }
    }
}