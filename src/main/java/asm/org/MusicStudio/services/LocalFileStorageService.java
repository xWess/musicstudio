package asm.org.MusicStudio.services;

import asm.org.MusicStudio.config.FileUploadConfig;
import asm.org.MusicStudio.util.FileUploadUtils;
import asm.org.MusicStudio.exception.FileUploadException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalFileStorageService implements FileStorageService {
    private static LocalFileStorageService instance;
    private final Path storageLocation;
    
    private LocalFileStorageService() {
        this.storageLocation = FileUploadConfig.getUploadPath();
        try {
            init();
        } catch (IOException e) {
            throw new FileUploadException("Could not initialize storage", e);
        }
    }
    
    public static LocalFileStorageService getInstance() {
        if (instance == null) {
            instance = new LocalFileStorageService();
        }
        return instance;
    }
    
    @Override
    public void init() throws IOException {
        Files.createDirectories(storageLocation);
    }
    
    @Override
    public Path store(File file) throws IOException {
        String uniqueFileName = FileUploadUtils.generateUniqueFileName(file.getName());
        Path targetPath = storageLocation.resolve(uniqueFileName);
        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return targetPath;
    }
    
    @Override
    public void delete(String filePath) throws IOException {
        Path path = Path.of(filePath);
        Files.deleteIfExists(path);
    }
    
    @Override
    public Path getStoragePath() {
        return storageLocation;
    }
    
    @Override
    public boolean exists(String filePath) {
        return Files.exists(Path.of(filePath));
    }
} 