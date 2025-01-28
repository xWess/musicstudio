package asm.org.MusicStudio;

import asm.org.MusicStudio.db.DatabaseConnection;
import java.sql.Connection;

public class MusicStudioApplication {
    public static void main(String[] args) {
        System.out.println("Music Studio Application Started");
        
        DatabaseConnection dbConnection = null;
        Connection conn = null;
        try {
            dbConnection = DatabaseConnection.getInstance();
            conn = dbConnection.getConnection();
            System.out.println("Database connection successful!");
            
            // Test if connection is valid
            if (conn.isValid(5)) {
                System.out.println("Connection is valid");
            }
            
        } catch (Exception e) {
            System.err.println("Connection Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (dbConnection != null) {
                dbConnection.closeConnection();
            }
        }
        
        // Add a small delay to allow thread cleanup
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}