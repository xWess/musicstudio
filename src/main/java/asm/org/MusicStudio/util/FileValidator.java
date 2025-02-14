package asm.org.MusicStudio.util;

import asm.org.MusicStudio.config.FileUploadConfig;
import asm.org.MusicStudio.exception.FileUploadException;
import java.io.File;

public class FileValidator {
    public static void validateFile(File file) {
        if (file == null) {
            throw new FileUploadException("File cannot be null");
        }
        
        if (!file.exists()) {
            throw new FileUploadException("File does not exist: " + file.getPath());
        }
        
        if (file.length() > FileUploadConfig.getMaxFileSize()) {
            throw new FileUploadException("File size exceeds maximum allowed size of " + 
                (FileUploadConfig.getMaxFileSize() / (1024 * 1024)) + "MB");
        }
        
        String extension = getFileExtension(file);
        if (!FileUploadConfig.isAllowedExtension(extension)) {
            throw new FileUploadException("File type not allowed. Allowed types: pdf, doc, docx, txt, mp3, wav, jpg, png");
        }
    }
    
    public static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; 
        }
        return name.substring(lastIndexOf + 1).toLowerCase();
    }
} 