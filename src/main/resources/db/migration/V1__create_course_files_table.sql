CREATE TABLE IF NOT EXISTS course_files (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    course_id INTEGER NOT NULL,
    teacher_id INTEGER NOT NULL,
    file_type VARCHAR(10) NOT NULL,
    description TEXT,
    file_size BIGINT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
); 