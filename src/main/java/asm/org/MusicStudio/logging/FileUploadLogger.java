package asm.org.MusicStudio.logging;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadLogger {
    private static final String LOG_FILE = "logs/fileupload.log";
    private static final Logger logger = Logger.getLogger(FileUploadLogger.class.getName());
    
    static {
        try {
            Path logPath = Paths.get("logs");
            if (!logPath.toFile().exists()) {
                logPath.toFile().mkdirs();
            }
            
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Could not initialize file upload logger: " + e.getMessage());
        }
    }
    
    private FileUploadLogger() {} // Prevent instantiation
    
    public static void info(String message) {
        logger.info(message);
    }
    
    public static void warn(String message) {
        logger.warning(message);
    }
    
    public static void error(String message, Throwable e) {
        logger.log(Level.SEVERE, message, e);
    }
    
    public static void uploadStarted(String fileName, String teacherId) {
        logger.info(String.format("File upload started - File: %s, Teacher: %s", fileName, teacherId));
    }
    
    public static void uploadCompleted(String fileName, String filePath) {
        logger.info(String.format("File upload completed - File: %s, Path: %s", fileName, filePath));
    }
    
    public static void uploadFailed(String fileName, String reason) {
        logger.warning(String.format("File upload failed - File: %s, Reason: %s", fileName, reason));
    }
} 