package asm.org.MusicStudio.event;

import asm.org.MusicStudio.service.FileUploadResult;
import java.util.ArrayList;
import java.util.List;

public class FileUploadEventPublisher {
    private static FileUploadEventPublisher instance;
    private final List<FileUploadListener> listeners;
    
    private FileUploadEventPublisher() {
        this.listeners = new ArrayList<>();
    }
    
    public static FileUploadEventPublisher getInstance() {
        if (instance == null) {
            instance = new FileUploadEventPublisher();
        }
        return instance;
    }
    
    public void addListener(FileUploadListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(FileUploadListener listener) {
        listeners.remove(listener);
    }
    
    public void publishResult(FileUploadResult result) {
        FileUploadEvent event = new FileUploadEvent(
            result.getFile(),
            result.isSuccess() ? "COMPLETED" : "FAILED",
            result.getMessage()
        );
        
        for (FileUploadListener listener : listeners) {
            if (result.isSuccess()) {
                listener.onUploadCompleted(event);
            } else {
                listener.onUploadFailed(event);
            }
        }
    }
    
    public void publishProgress(String message) {
        FileUploadEvent event = new FileUploadEvent(null, "PROGRESS", message);
        for (FileUploadListener listener : listeners) {
            listener.onUploadProgress(event);
        }
    }
} 