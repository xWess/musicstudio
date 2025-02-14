-- Create course_files table
CREATE TABLE IF NOT EXISTS course_files (
    id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL REFERENCES courses(id),
    student_id INTEGER NOT NULL REFERENCES users(id),
    teacher_id INTEGER NOT NULL REFERENCES users(id),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    file_type VARCHAR(50),
    file_size BIGINT,
    description TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    
    CONSTRAINT fk_course_files_course
        FOREIGN KEY (course_id) 
        REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_files_student
        FOREIGN KEY (student_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_course_files_teacher
        FOREIGN KEY (teacher_id) 
        REFERENCES users(id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_course_files_lookup 
ON course_files(course_id, student_id, teacher_id); 