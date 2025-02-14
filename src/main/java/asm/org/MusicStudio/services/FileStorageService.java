package asm.org.MusicStudio.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {
    Path store(File file) throws IOException;
    void delete(String filePath) throws IOException;
    Path getStoragePath();
    boolean exists(String filePath);
    void init() throws IOException;
} 