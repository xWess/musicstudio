package asm.org.MusicStudio.event;

public interface FileUploadListener {
    default void onUploadStarted(FileUploadEvent event) {}
    default void onUploadProgress(FileUploadEvent event) {}
    default void onUploadCompleted(FileUploadEvent event) {}
    default void onUploadFailed(FileUploadEvent event) {}
} 