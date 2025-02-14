package asm.org.MusicStudio.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import asm.org.MusicStudio.constants.FileManagementConstants;
import asm.org.MusicStudio.dao.CourseFileDAO;
import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.exceptions.FileUploadException;

public class CourseFileService {
    private static final String uploadDirectory = "uploads/";
    private final CourseFileDAO courseFileDAO;

    public CourseFileService() {
        this.courseFileDAO = new CourseFileDAO();
        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(uploadDirectory));
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
        }
    }

    public CourseFile uploadFile(File file, int courseId, int teacherId, String description) {
        if (file.length() > FileManagementConstants.MAX_FILE_SIZE) {
            throw new FileUploadException(FileManagementConstants.ERROR_FILE_TOO_LARGE);
        }

        try {
            validateMimeType(file);
            
            // Create course-specific directory
            String courseDir = uploadDirectory + "course_" + courseId + "/";
            Files.createDirectories(Paths.get(courseDir));
            
            // Generate unique filename to prevent overwrites
            String uniqueFileName = System.currentTimeMillis() + "_" + file.getName();
            Path targetPath = Paths.get(courseDir, uniqueFileName);
            
            // Copy file to target location
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Create database entry
            CourseFile courseFile = CourseFile.builder()
                .fileName(file.getName())
                .filePath(targetPath.toString())
                .courseId(courseId)
                .teacherId(teacherId)
                .fileType(getFileExtension(file.getName()).toUpperCase())
                .description(description)
                .fileSize(file.length())
                .build();
                
            courseFileDAO.insertFile(courseFile);
            return courseFile;
            
        } catch (IOException | SQLException e) {
            throw new FileUploadException(FileManagementConstants.ERROR_UPLOAD_FAILED, e);
        }
    }

    public List<CourseFile> getFilesByCourse(int courseId) {
        try {
            return courseFileDAO.findByCourseId(courseId);
        } catch (SQLException e) {
            System.err.println("Error fetching course files: " + e.getMessage());
            return List.of();
        }
    }

    public List<CourseFile> getFilesByTeacher(int teacherId) {
        try {
            return courseFileDAO.getFilesByTeacher(teacherId);
        } catch (SQLException e) {
            System.err.println("Error fetching teacher files: " + e.getMessage());
            return List.of();
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    private void validateFileType(String fileName) {
        String extension = getFileExtension(fileName).toUpperCase();
        boolean isValidType = extension.equals(FileManagementConstants.FILE_TYPE_PDF) ||
                            extension.equals(FileManagementConstants.FILE_TYPE_DOC) ||
                            extension.equals(FileManagementConstants.FILE_TYPE_DOCX) ||
                            extension.equals(FileManagementConstants.FILE_TYPE_TXT);
                            
        if (!isValidType) {
            throw new FileUploadException(FileManagementConstants.ERROR_INVALID_FILE_TYPE);
        }
    }

    private void validateFile(File file) {
        if (file == null) {
            throw new FileUploadException("File cannot be null");
        }
        
        // Validate file exists
        if (!file.exists()) {
            throw new FileUploadException("File does not exist");
        }
        
        // Validate file size
        if (getFileSize(file) > FileManagementConstants.MAX_FILE_SIZE) {
            throw new FileUploadException(FileManagementConstants.ERROR_FILE_TOO_LARGE);
        }
        
        // Validate file type
        String extension = getFileExtension(file.getName()).toUpperCase();
        if (!isValidFileType(extension)) {
            throw new FileUploadException(FileManagementConstants.ERROR_INVALID_FILE_TYPE);
        }
        
        try {
            validateMimeType(file);
            validateFileContent(file);
        } catch (IOException e) {
            throw new FileUploadException("Error validating file: " + e.getMessage());
        }
    }

    private boolean isValidFileType(String fileType) {
        return fileType.equals(FileManagementConstants.FILE_TYPE_PDF) ||
               fileType.equals(FileManagementConstants.FILE_TYPE_DOC) ||
               fileType.equals(FileManagementConstants.FILE_TYPE_DOCX) ||
               fileType.equals(FileManagementConstants.FILE_TYPE_TXT);
    }

    private long getFileSize(File file) {
        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            throw new FileUploadException("Could not determine file size", e);
        }
    }

    public void deleteFile(CourseFile file) throws IOException, SQLException {
        // Delete physical file
        Path filePath = Paths.get(file.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // Delete database record
        courseFileDAO.deleteFile(file.getId());
    }

    public List<Integer> getTeacherCourseIds(int teacherId) {
        try {
            return courseFileDAO.findCourseIdsByTeacher(teacherId);
        } catch (SQLException e) {
            System.err.println("Error fetching teacher courses: " + e.getMessage());
            return List.of();
        }
    }

    private void validateMimeType(File file) throws IOException {
        String mimeType = Files.probeContentType(file.toPath());
        boolean isValidMime = mimeType != null && (
            mimeType.equals(FileManagementConstants.MIME_TYPE_PDF) ||
            mimeType.equals(FileManagementConstants.MIME_TYPE_DOC) ||
            mimeType.equals(FileManagementConstants.MIME_TYPE_DOCX) ||
            mimeType.equals(FileManagementConstants.MIME_TYPE_TXT)
        );
        
        if (!isValidMime) {
            throw new FileUploadException(FileManagementConstants.ERROR_INVALID_FILE_TYPE);
        }
    }

    public interface DownloadProgressCallback {
        void onProgress(double progress);
    }

    public void downloadFile(CourseFile file, String targetPath, DownloadProgressCallback callback) throws IOException {
        Path sourcePath = Paths.get(file.getFilePath());
        if (!Files.exists(sourcePath)) {
            throw new FileUploadException(FileManagementConstants.ERROR_FILE_NOT_FOUND);
        }
        
        Path targetFilePath = Paths.get(targetPath, file.getFileName());
        long fileSize = Files.size(sourcePath);
        
        try (var input = Files.newInputStream(sourcePath);
             var output = Files.newOutputStream(targetFilePath)) {
            
            byte[] buffer = new byte[8192];
            long totalBytesRead = 0;
            int bytesRead;
            
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                if (callback != null) {
                    callback.onProgress((double) totalBytesRead / fileSize);
                }
            }
        }
    }

    private void validateFileContent(File file) throws IOException {
        if (file.length() < FileManagementConstants.MIN_FILE_SIZE) {
            throw new FileUploadException(FileManagementConstants.ERROR_EMPTY_FILE);
        }

        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new FileUploadException(FileManagementConstants.ERROR_NO_EXTENSION);
        }

        String extension = fileName.substring(lastDotIndex).toLowerCase();
        boolean isValidExtension = Arrays.stream(FileManagementConstants.ALLOWED_EXTENSIONS)
            .anyMatch(ext -> ext.equals(extension));
            
        if (!isValidExtension) {
            throw new FileUploadException(FileManagementConstants.ERROR_INVALID_FILE_TYPE);
        }
    }

    public void downloadFile(CourseFile file, String targetPath) throws IOException {
        Path sourcePath = Paths.get(file.getFilePath());
        if (!Files.exists(sourcePath)) {
            throw new FileUploadException(FileManagementConstants.ERROR_FILE_NOT_FOUND);
        }
        
        Path targetFilePath = Paths.get(targetPath, file.getFileName());
        Files.copy(sourcePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
    }
} 