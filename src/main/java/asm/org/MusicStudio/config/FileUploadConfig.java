package asm.org.MusicStudio.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

public class FileUploadConfig {
    private static final String BASE_UPLOAD_DIR = "uploads/course_files";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    private static final String DEFAULT_ALLOWED_EXTENSIONS = "pdf,doc,docx,txt,mp3,wav,jpg,png";
    
    public static Path getUploadPath() {
        Path uploadPath = Paths.get(BASE_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
        }
        return uploadPath;
    }
    
    public static boolean isAllowedExtension(String extension) {
        if (extension == null) return false;
        String allowedExtensions = FileUploadConfigLoader.getProperty(
            "upload.allowedExtensions", 
            DEFAULT_ALLOWED_EXTENSIONS
        );
        return allowedExtensions.toLowerCase().contains(extension.toLowerCase());
    }

    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
} 