package asm.org.MusicStudio.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FileUploadMetadata {
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private String uploadedBy;
    private String contentType;
    private String checksum;
    
    public static FileUploadMetadata from(String fileName, Long size, String type) {
        return FileUploadMetadata.builder()
            .originalFileName(fileName)
            .fileSize(size)
            .fileType(type)
            .uploadDate(LocalDateTime.now())
            .build();
    }
} 