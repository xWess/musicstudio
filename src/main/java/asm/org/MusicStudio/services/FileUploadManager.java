package asm.org.MusicStudio.services;

import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.event.FileUploadEvent;
import asm.org.MusicStudio.event.FileUploadListener;
import asm.org.MusicStudio.util.FileValidator;
import asm.org.MusicStudio.enums.FileUploadStatus;
import asm.org.MusicStudio.service.FileUploadResult;
import asm.org.MusicStudio.dao.CourseDAO;
import asm.org.MusicStudio.exception.FileUploadException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.SQLException;
import java.io.IOException;

public class FileUploadManager {
    private static FileUploadManager instance;
    private final FileUploadService fileService;
    private final CourseDAO courseDAO;
    private final List<FileUploadListener> listeners;
    private final ExecutorService executorService;
    
    private FileUploadManager() {
        this.fileService = FileUploadService.getInstance();
        this.courseDAO = new CourseDAO();
        this.listeners = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(3);
    }
    
    public static FileUploadManager getInstance() {
        if (instance == null) {
            instance = new FileUploadManager();
        }
        return instance;
    }
    
    public void addListener(FileUploadListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(FileUploadListener listener) {
        listeners.remove(listener);
    }
    
    public void handleFileUpload(File file, Integer courseId, Integer teacherId, String description) {
        try {
            fileService.uploadFile(file, courseId, teacherId, description);
            // Handle success
            notifySuccess("File uploaded successfully");
        } catch (IOException | SQLException e) {
            // Handle error
            notifyError("Failed to upload file: " + e.getMessage());
        }
    }
    
    private void notifyListeners(FileUploadEvent event) {
        for (FileUploadListener listener : listeners) {
            switch (event.getStatus()) {
                case "UPLOADING" -> listener.onUploadStarted(event);
                case "COMPLETED" -> listener.onUploadCompleted(event);
                case "FAILED" -> listener.onUploadFailed(event);
                default -> listener.onUploadProgress(event);
            }
        }
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
    
    private void notifySuccess(String message) {
        FileUploadEvent event = new FileUploadEvent(null, FileUploadStatus.COMPLETED.name(), message);
        notifyListeners(event);
    }

    private void notifyError(String message) {
        FileUploadEvent event = new FileUploadEvent(null, FileUploadStatus.FAILED.name(), message);
        notifyListeners(event);
    }
    
    private CourseFile processFileUpload(File file, Integer courseId, Integer teacherId, String description) {
        try {
            // Verify course exists
            var course = courseDAO.findById(courseId);
            if (course == null) {
                throw new FileUploadException("Course not found");
            }
            
            // Upload file
            fileService.uploadFile(file, courseId, teacherId, description);
            
            // Create CourseFile
            CourseFile courseFile = CourseFile.builder()
                .fileName(file.getName())
                .filePath(file.getAbsolutePath())
                .courseId(courseId)
                .teacherId(teacherId)
                .description(description)
                .build();
                
            courseFile.setCourse(course);
            return courseFile;
            
        } catch (IOException | SQLException e) {
            throw new FileUploadException("Error during file upload", e);
        }
    }
    
    public void handleFileDelete(CourseFile file) {
        try {
            fileService.deleteFile(file);
            // Handle success
            notifySuccess("File deleted successfully");
        } catch (IOException | SQLException e) {
            // Handle error
            notifyError("Failed to delete file: " + e.getMessage());
        }
    }
} 