package asm.org.MusicStudio.model;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class FileUploadStatus {
    private final String fileName;
    private final long totalBytes;
    private final long uploadedBytes;
    private final String status;
    private final double progress;
    private final String message;
    
    public static FileUploadStatus started(String fileName, long totalBytes) {
        return FileUploadStatus.builder()
            .fileName(fileName)
            .totalBytes(totalBytes)
            .uploadedBytes(0)
            .status("STARTED")
            .progress(0.0)
            .message("Upload started")
            .build();
    }
    
    public static FileUploadStatus inProgress(String fileName, long totalBytes, long uploadedBytes) {
        double progress = totalBytes > 0 ? (double) uploadedBytes / totalBytes * 100 : 0;
        return FileUploadStatus.builder()
            .fileName(fileName)
            .totalBytes(totalBytes)
            .uploadedBytes(uploadedBytes)
            .status("IN_PROGRESS")
            .progress(progress)
            .message(String.format("Uploading: %.1f%%", progress))
            .build();
    }
    
    public static FileUploadStatus completed(String fileName) {
        return FileUploadStatus.builder()
            .fileName(fileName)
            .status("COMPLETED")
            .progress(100.0)
            .message("Upload completed")
            .build();
    }
    
    public static FileUploadStatus failed(String fileName, String error) {
        return FileUploadStatus.builder()
            .fileName(fileName)
            .status("FAILED")
            .progress(0.0)
            .message("Upload failed: " + error)
            .build();
    }
} 