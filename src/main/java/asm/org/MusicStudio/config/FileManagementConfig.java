package asm.org.MusicStudio.config;

public class FileManagementConfig {
    public static final String UPLOAD_BASE_DIR = "uploads/courses/";
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_FILE_TYPES = {
        "pdf", "doc", "docx", "txt", "jpg", "jpeg", "png"
    };
    
    public static boolean isAllowedFileType(String extension) {
        for (String type : ALLOWED_FILE_TYPES) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
} 