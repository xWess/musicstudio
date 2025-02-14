package asm.org.MusicStudio.constants;

public class FileManagementConstants {
    // File size limits
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final long MIN_FILE_SIZE = 1L; // 1 byte minimum
    
    // Error messages
    public static final String ERROR_FILE_TOO_LARGE = "File size exceeds maximum limit of 10MB";
    public static final String ERROR_UPLOAD_FAILED = "Failed to upload file";
    public static final String ERROR_INVALID_FILE_TYPE = "Invalid file type";
    public static final String ERROR_FILE_NOT_FOUND = "File not found";
    public static final String ERROR_NO_EXTENSION = "File must have an extension";
    public static final String ERROR_EMPTY_FILE = "File cannot be empty";
    
    // MIME types
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String MIME_TYPE_DOC = "application/msword";
    public static final String MIME_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MIME_TYPE_TXT = "text/plain";
    
    // File types
    public static final String FILE_TYPE_PDF = "PDF";
    public static final String FILE_TYPE_DOC = "DOC";
    public static final String FILE_TYPE_DOCX = "DOCX";
    public static final String FILE_TYPE_TXT = "TXT";
    
    // Success messages
    public static final String SUCCESS_UPLOAD = "File uploaded successfully";
    public static final String SUCCESS_DELETE = "File deleted successfully";
    public static final String SUCCESS_DOWNLOAD = "File downloaded successfully";
    
    // File validation
    public static final String[] ALLOWED_EXTENSIONS = {".pdf", ".doc", ".docx", ".txt"};
}