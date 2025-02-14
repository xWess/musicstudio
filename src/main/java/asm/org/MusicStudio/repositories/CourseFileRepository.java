package asm.org.MusicStudio.repositories;

import java.util.List;

import asm.org.MusicStudio.entity.CourseFile;

public interface CourseFileRepository {
    void save(CourseFile file);
    void delete(CourseFile file);
    CourseFile findById(Long id);
    List<CourseFile> findByTeacherId(Integer teacherId);
    List<CourseFile> findByCourseIdForStudent(Integer courseId);
    List<CourseFile> findByStudentEnrollment(Integer studentId);
} 