package asm.org.MusicStudio.repository;

import asm.org.MusicStudio.constants.FileUploadConstants;

public final class FileUploadQueries {
    private FileUploadQueries() {} // Prevent instantiation
    
    public static final String INSERT_FILE = """
        INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING %s
        """.formatted(
            FileUploadConstants.TABLE_NAME,
            FileUploadConstants.Columns.COURSE_ID,
            FileUploadConstants.Columns.TEACHER_ID,
            FileUploadConstants.Columns.FILE_NAME,
            FileUploadConstants.Columns.FILE_PATH,
            FileUploadConstants.Columns.FILE_TYPE,
            FileUploadConstants.Columns.FILE_SIZE,
            FileUploadConstants.Columns.DESCRIPTION,
            FileUploadConstants.Columns.STATUS,
            FileUploadConstants.Columns.ID
        );
        
    public static final String FIND_BY_TEACHER = """
        SELECT cf.*, c.name as course_name, u.name as teacher_name
        FROM %s cf
        JOIN courses c ON cf.course_id = c.id
        JOIN users u ON cf.teacher_id = u.id
        WHERE cf.teacher_id = ? AND cf.status = ?
        ORDER BY cf.upload_date DESC
        """.formatted(FileUploadConstants.TABLE_NAME);
        
    public static final String DELETE_FILE = 
        "DELETE FROM %s WHERE %s = ?".formatted(
            FileUploadConstants.TABLE_NAME,
            FileUploadConstants.Columns.ID
        );
} 