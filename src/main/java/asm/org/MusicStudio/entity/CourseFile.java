package asm.org.MusicStudio.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseFile {
    private Integer id;
    private String fileName;
    private String filePath;
    @Builder.Default
    private LocalDateTime uploadDate = LocalDateTime.now();
    private Integer courseId;
    private Integer teacherId;
    private String fileType;
    private String description;
    private Course course;
    private User teacher;
    private long fileSize;

    public static class CourseFileBuilder {
        private long fileSize;
        
        public CourseFileBuilder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFormattedSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        }
    }
} 