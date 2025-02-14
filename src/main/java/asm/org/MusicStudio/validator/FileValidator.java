package asm.org.MusicStudio.validator;

import java.io.File;

import asm.org.MusicStudio.config.FileManagementConfig;
import asm.org.MusicStudio.exception.FileManagementException;

public class FileValidator {
    
    public static void validateFile(File file) {
        if (file == null) {
            throw new FileManagementException("No file selected");
        }

        // Check file size
        if (file.length() > FileManagementConfig.MAX_FILE_SIZE) {
            throw new FileManagementException("File size exceeds maximum limit of 10MB");
        }

        // Check file extension
        String extension = getFileExtension(file.getName());
        if (!FileManagementConfig.isAllowedFileType(extension)) {
            throw new FileManagementException("File type not allowed. Allowed types: " + 
                String.join(", ", FileManagementConfig.ALLOWED_FILE_TYPES));
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
    }
} 