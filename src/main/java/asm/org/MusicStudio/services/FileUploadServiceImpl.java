package asm.org.MusicStudio.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import asm.org.MusicStudio.config.FileUploadConfig;
import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.repositories.CourseFileRepository;
import asm.org.MusicStudio.repositories.CourseFileRepositoryImpl;

public class FileUploadServiceImpl implements FileUploadService {

    private static FileUploadService instance;
    private final CourseFileRepository repository;

    private FileUploadServiceImpl(CourseFileRepository repository) {
        this.repository = repository;
    }

    public static FileUploadService getInstance() {
        if (instance == null) {
            instance = new FileUploadServiceImpl(new CourseFileRepositoryImpl()); // Vous devrez créer cette implémentation
        }
        return instance;
    }

    private void ensureUploadDirectoryExists() {
        Path uploadPath = FileUploadConfig.getUploadPath();
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
        }
    }

    @Override
    public void uploadFile(File file, Integer courseId, Integer teacherId, String description) 
        throws IOException, SQLException {
        
        ensureUploadDirectoryExists();
        
        // Generate unique filename
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getName();
        Path targetPath = FileUploadConfig.getUploadPath().resolve(uniqueFileName);
        
        // Copy file to upload directory
        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Create database record
        CourseFile courseFile = CourseFile.builder()
            .fileName(file.getName())
            .filePath(targetPath.toString())
            .fileType(getFileExtension(file))
            .fileSize(file.length())
            .description(description)
            .courseId(courseId)
            .teacherId(teacherId)
            .build();
            
        repository.save(courseFile);
    }

    @Override
    public void downloadFile(CourseFile courseFile, File targetFile) 
        throws IOException, SQLException {
        Path sourcePath = Path.of(courseFile.getFilePath());
        if (!Files.exists(sourcePath)) {
            throw new IOException("Source file not found: " + courseFile.getFilePath());
        }
        Files.copy(sourcePath, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void deleteFile(CourseFile courseFile) 
        throws IOException, SQLException {
        Path filePath = Path.of(courseFile.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        repository.delete(courseFile);
    }

    @Override
    public void cleanup() throws IOException, SQLException {
        // Implementation needed
        // Delete orphaned files that are no longer referenced in the database
    }

    @Override
    public List<CourseFile> getFilesByTeacher(Integer teacherId) 
        throws SQLException {
        return repository.findByTeacherId(teacherId);
    }

    @Override
    public List<CourseFile> getFilesByStudentEnrollment(Integer studentId) throws SQLException {
        return repository.findByStudentEnrollment(studentId);
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1);
        }
        return "";
    }
} 