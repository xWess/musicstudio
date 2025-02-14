package asm.org.MusicStudio.constants;

public final class FileUploadConstants {
    private FileUploadConstants() {} // Prevent instantiation
    
    public static final String DEFAULT_UPLOAD_DIR = "course_files";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String TABLE_NAME = "course_files";
    
    public static final class Columns {
        private Columns() {}
        
        public static final String ID = "id";
        public static final String COURSE_ID = "course_id";
        public static final String TEACHER_ID = "teacher_id";
        public static final String FILE_NAME = "file_name";
        public static final String FILE_PATH = "file_path";
        public static final String FILE_TYPE = "file_type";
        public static final String FILE_SIZE = "file_size";
        public static final String DESCRIPTION = "description";
        public static final String STATUS = "status";
        public static final String UPLOAD_DATE = "upload_date";
    }
    
    public static final class ErrorMessages {
        private ErrorMessages() {}
        
        public static final String FILE_NOT_FOUND = "File not found: %s";
        public static final String UPLOAD_FAILED = "Failed to upload file: %s";
        public static final String DELETE_FAILED = "Failed to delete file: %s";
        public static final String INVALID_FILE_TYPE = "Invalid file type: %s";
    }
} 