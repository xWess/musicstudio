package asm.org.MusicStudio.cache;

import asm.org.MusicStudio.entity.CourseFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FileUploadCache {
    private static final int CACHE_DURATION_MINUTES = 30;
    private static FileUploadCache instance;
    private final Map<Integer, CacheEntry> cache;
    
    private FileUploadCache() {
        this.cache = new ConcurrentHashMap<>();
    }
    
    public static FileUploadCache getInstance() {
        if (instance == null) {
            instance = new FileUploadCache();
        }
        return instance;
    }
    
    public void put(Integer teacherId, CourseFile file) {
        cache.put(file.getId(), new CacheEntry(file));
    }
    
    public CourseFile get(Integer fileId) {
        CacheEntry entry = cache.get(fileId);
        if (entry != null && !entry.isExpired()) {
            return entry.getFile();
        }
        cache.remove(fileId);
        return null;
    }
    
    public void remove(Integer fileId) {
        cache.remove(fileId);
    }
    
    public void clear() {
        cache.clear();
    }
    
    private static class CacheEntry {
        private final CourseFile file;
        private final LocalDateTime timestamp;
        
        CacheEntry(CourseFile file) {
            this.file = file;
            this.timestamp = LocalDateTime.now();
        }
        
        boolean isExpired() {
            return ChronoUnit.MINUTES.between(timestamp, LocalDateTime.now()) 
                > CACHE_DURATION_MINUTES;
        }
        
        CourseFile getFile() {
            return file;
        }
    }
} 