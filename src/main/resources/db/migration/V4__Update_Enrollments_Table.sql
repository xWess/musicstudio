-- Add schedule column if it doesn't exist
ALTER TABLE courses 
    ADD COLUMN IF NOT EXISTS schedule VARCHAR(100);

-- Update enrollments table with additional fields if they don't exist
ALTER TABLE enrollments 
    ADD COLUMN IF NOT EXISTS start_date DATE,
    ADD COLUMN IF NOT EXISTS end_date DATE;

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_enrollments_student ON enrollments(student_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_course ON enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_courses_teacher ON courses(teacher_id); 