package asm.org.MusicStudio.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class FileUploadConfigLoader {
    private static final Logger logger = Logger.getLogger(FileUploadConfigLoader.class.getName());
    private static Properties properties;
    
    static {
        loadConfig();
    }
    
    private FileUploadConfigLoader() {} // Prevent instantiation
    
    private static void loadConfig() {
        properties = new Properties();
        try (InputStream input = FileUploadConfigLoader.class.getClassLoader()
                .getResourceAsStream("fileupload.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("File upload configuration loaded successfully");
            } else {
                logger.warning("fileupload.properties not found, using defaults");
            }
        } catch (IOException e) {
            logger.warning("Error loading fileupload.properties: " + e.getMessage());
        }
    }
    
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static long getLongProperty(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warning("Invalid number format for property " + key + ", using default");
            return defaultValue;
        }
    }
    
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
} 