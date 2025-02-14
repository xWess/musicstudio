package asm.org.MusicStudio.exception;

import asm.org.MusicStudio.constants.FileUploadConstants.ErrorMessages;
import java.io.IOException;
import java.sql.SQLException;

public class FileUploadExceptionHandler {
    private FileUploadExceptionHandler() {}
    
    public static FileUploadException handleException(Exception e) {
        if (e instanceof SQLException) {
            return new FileUploadException("Database error during file operation", e);
        } else if (e instanceof IOException) {
            return new FileUploadException("File system error during file operation", e);
        } else {
            return new FileUploadException("Unexpected error during file operation", e);
        }
    }
    
    public static FileUploadException fileNotFound(String fileName) {
        return new FileUploadException(String.format(ErrorMessages.FILE_NOT_FOUND, fileName));
    }
    
    public static FileUploadException uploadFailed(String reason) {
        return new FileUploadException(String.format(ErrorMessages.UPLOAD_FAILED, reason));
    }
    
    public static FileUploadException deleteFailed(String fileName) {
        return new FileUploadException(String.format(ErrorMessages.DELETE_FAILED, fileName));
    }
} 