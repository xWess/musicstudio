package asm.org.MusicStudio.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class FileUploadResponse {
    private boolean success;
    private String message;
    private String filePath;
    private String fileName;
    
    public static FileUploadResponse success(String filePath, String fileName) {
        return FileUploadResponse.builder()
            .success(true)
            .message("File uploaded successfully")
            .filePath(filePath)
            .fileName(fileName)
            .build();
    }
    
    public static FileUploadResponse error(String message) {
        return FileUploadResponse.builder()
            .success(false)
            .message(message)
            .build();
    }
} 