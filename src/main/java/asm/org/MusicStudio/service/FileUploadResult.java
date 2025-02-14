package asm.org.MusicStudio.service;

import asm.org.MusicStudio.entity.CourseFile;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResult {
    private boolean success;
    private String message;
    private String filePath;
    private String fileName;
    private String error;
    private CourseFile file;
    
    public static FileUploadResult success(String filePath, String fileName, CourseFile file) {
        return FileUploadResult.builder()
            .success(true)
            .filePath(filePath)
            .fileName(fileName)
            .file(file)
            .message("File uploaded successfully")
            .build();
    }
    
    public static FileUploadResult failure(String error) {
        return FileUploadResult.builder()
            .success(false)
            .error(error)
            .message("File upload failed")
            .build();
    }
} 