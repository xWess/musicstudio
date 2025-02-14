package asm.org.MusicStudio.services;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import asm.org.MusicStudio.entity.CourseFile;

public interface FileUploadService {
    void uploadFile(File file, Integer courseId, Integer teacherId, String description) 
        throws IOException, SQLException;
    void downloadFile(CourseFile courseFile, File targetFile) 
        throws IOException, SQLException;
    void deleteFile(CourseFile courseFile) 
        throws IOException, SQLException;
    void cleanup() throws IOException, SQLException;
    List<CourseFile> getFilesByTeacher(Integer teacherId) throws SQLException;
    List<CourseFile> getFilesByStudentEnrollment(Integer studentId) throws SQLException;
    
    static FileUploadService getInstance() {
        return FileUploadServiceImpl.getInstance();
    }
} 