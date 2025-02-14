package asm.org.MusicStudio.validator;

import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.exception.FileUploadException;
import asm.org.MusicStudio.config.FileUploadConfig;

public class FileUploadValidator {
    private FileUploadValidator() {}
    
    public static void validateCourseFile(CourseFile file) {
        if (file == null) {
            throw new FileUploadException("Course file cannot be null");
        }
        
        if (file.getCourseId() == null || file.getCourseId() <= 0) {
            throw new FileUploadException("Invalid course ID");
        }
        
        if (file.getTeacherId() == null || file.getTeacherId() <= 0) {
            throw new FileUploadException("Invalid teacher ID");
        }
        
        if (file.getFileName() == null || file.getFileName().trim().isEmpty()) {
            throw new FileUploadException("File name cannot be empty");
        }
        
        if (file.getFileSize() == null || file.getFileSize() <= 0) {
            throw new FileUploadException("Invalid file size");
        }
        
        if (file.getFileSize() > FileUploadConfig.getMaxFileSize()) {
            throw new FileUploadException("File size exceeds maximum allowed size");
        }
    }
    
    public static void validateFileType(String fileType) {
        if (fileType == null || !FileUploadConfig.isAllowedExtension(fileType)) {
            throw new FileUploadException("Invalid or unsupported file type: " + fileType);
        }
    }
} 