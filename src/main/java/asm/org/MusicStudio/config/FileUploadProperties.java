package asm.org.MusicStudio.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileUploadProperties {
    private static Properties properties;
    
    static {
        properties = new Properties();
        try (InputStream input = FileUploadProperties.class.getClassLoader()
                .getResourceAsStream("fileupload.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            // Use defaults if properties file not found
        }
    }
    
    public static String getUploadDir() {
        return properties.getProperty("upload.dir", "course_files");
    }
    
    public static long getMaxFileSize() {
        return Long.parseLong(properties.getProperty("upload.maxSize", "52428800")); // 50MB default
    }
    
    public static String getAllowedExtensions() {
        return properties.getProperty("upload.allowedExtensions", 
            "pdf,doc,docx,txt,mp3,wav,jpg,png");
    }
} 