package asm.org.MusicStudio.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import asm.org.MusicStudio.config.FileManagementConfig;
import asm.org.MusicStudio.exception.FileManagementException;

public class FileOperationHelper {
    
    public static String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return timestamp + "_" + originalFileName.substring(0, originalFileName.lastIndexOf('.')) + extension;
    }
    
    public static String saveFile(File sourceFile, int courseId) throws IOException {
        String uniqueFileName = generateUniqueFileName(sourceFile.getName());
        String courseDir = FileManagementConfig.UPLOAD_BASE_DIR + "course" + courseId + "/";
        
        // Create course directory if it doesn't exist
        Files.createDirectories(Paths.get(courseDir));
        
        // Copy file to destination
        Path destinationPath = Paths.get(courseDir + uniqueFileName);
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        
        return destinationPath.toString();
    }
    
    public static void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        } else {
            throw new FileManagementException("File not found: " + filePath);
        }
    }
    
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
} 