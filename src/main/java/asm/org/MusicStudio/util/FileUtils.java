package asm.org.MusicStudio.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    private static final String BASE_UPLOAD_DIR = "uploads/courses/";
    
    public static void initializeDirectories() {
        try {
            Files.createDirectories(Paths.get(BASE_UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create base upload directory: " + e.getMessage());
        }
    }
    
    public static String getCourseDirectory(int courseId) {
        return BASE_UPLOAD_DIR + "course" + courseId + "/";
    }
    
    public static boolean createCourseDirectory(int courseId) {
        try {
            Files.createDirectories(Paths.get(getCourseDirectory(courseId)));
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create course directory: " + e.getMessage());
            return false;
        }
    }
    
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toUpperCase() : "UNKNOWN";
    }
    
    public static boolean isValidFileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return extension.matches("pdf|doc|docx|txt|jpg|jpeg|png");
    }
} 