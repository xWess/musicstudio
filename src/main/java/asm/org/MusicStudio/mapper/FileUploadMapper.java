package asm.org.MusicStudio.mapper;

import asm.org.MusicStudio.entity.CourseFile;
import asm.org.MusicStudio.constants.FileUploadConstants.Columns;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileUploadMapper {
    private FileUploadMapper() {} // Prevent instantiation
    
    public static CourseFile mapFromResultSet(ResultSet rs) throws SQLException {
        return CourseFile.builder()
            .id(rs.getInt(Columns.ID))
            .fileName(rs.getString(Columns.FILE_NAME))
            .filePath(rs.getString(Columns.FILE_PATH))
            .uploadDate(rs.getTimestamp(Columns.UPLOAD_DATE).toLocalDateTime())
            .courseId(rs.getInt(Columns.COURSE_ID))
            .teacherId(rs.getInt(Columns.TEACHER_ID))
            .fileType(rs.getString(Columns.FILE_TYPE))
            .description(rs.getString(Columns.DESCRIPTION))
            .fileSize(rs.getLong(Columns.FILE_SIZE))
            .courseName(rs.getString("course_name"))
            .teacherName(rs.getString("teacher_name"))
            .build();
    }
} 