package asm.org.MusicStudio.event;

import asm.org.MusicStudio.entity.CourseFile;
import lombok.Getter;

@Getter
public class FileUploadEvent {
    private final CourseFile file;
    private final String status;
    private final String message;
    private final double progress;
    
    public FileUploadEvent(CourseFile file, String status, String message) {
        this(file, status, message, -1);
    }
    
    public FileUploadEvent(CourseFile file, String status, String message, double progress) {
        this.file = file;
        this.status = status;
        this.message = message;
        this.progress = progress;
    }
} 