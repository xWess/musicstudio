package asm.org.MusicStudio.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class FileUploadUtils {
    private FileUploadUtils() {}
    
    private static final DateTimeFormatter FILE_DATE_FORMAT = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    public static String generateUniqueFileName(String originalName) {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
        String extension = getFileExtension(originalName);
        return String.format("%s_%s.%s", 
            removeSpecialChars(getFileNameWithoutExtension(originalName)),
            timestamp,
            extension);
    }
    
    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }
    
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }
    
    private static String removeSpecialChars(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }
    
    public static Path getRelativePath(Path basePath, Path fullPath) {
        return basePath.relativize(fullPath);
    }
} 