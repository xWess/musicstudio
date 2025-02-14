package asm.org.MusicStudio.services;

import asm.org.MusicStudio.config.FileUploadConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FileUploadCleanupService {
    private static FileUploadCleanupService instance;
    private final ScheduledExecutorService scheduler;
    private final FileUploadService fileUploadService;
    private static final Logger logger = Logger.getLogger(FileUploadCleanupService.class.getName());
    
    private FileUploadCleanupService() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.fileUploadService = FileUploadService.getInstance();
        startCleanupTask();
    }
    
    public static FileUploadCleanupService getInstance() {
        if (instance == null) {
            instance = new FileUploadCleanupService();
        }
        return instance;
    }
    
    private void startCleanupTask() {
        // Schedule cleanup at 2 AM every day
        scheduler.scheduleAtFixedRate(
            this::performCleanup,
            calculateInitialDelay(),
            24,
            TimeUnit.HOURS
        );
    }
    
    private void performCleanup() {
        try {
            Path uploadPath = FileUploadConfig.getUploadPath();
            if (Files.exists(uploadPath)) {
                fileUploadService.cleanup();
                logger.info("Scheduled cleanup completed successfully");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during cleanup: " + e.getMessage(), e);
        }
    }
    
    private long calculateInitialDelay() {
        // Calculate delay until next 2 AM
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime next2AM = now.withHour(2).withMinute(0).withSecond(0);
        if (now.compareTo(next2AM) > 0) {
            next2AM = next2AM.plusDays(1);
        }
        return java.time.Duration.between(now, next2AM).toMinutes();
    }
    
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Cleanup service shutdown interrupted", e);
        }
    }
} 