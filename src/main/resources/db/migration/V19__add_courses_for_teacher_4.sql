-- Add courses directly for teacher_id 4
INSERT INTO courses (name, teacher_id)
VALUES 
    ('Music Theory 101', 4),
    ('Piano Basics', 4),
    ('Guitar Masterclass', 4);

-- Add some sample files for the first course
DO $$ 
DECLARE
    first_course_id INTEGER;
BEGIN
    SELECT id INTO first_course_id FROM courses 
    WHERE teacher_id = 4 AND name = 'Music Theory 101';
    
    INSERT INTO course_files (file_name, file_path, course_id, teacher_id, file_type, description, file_size)
    VALUES 
        ('Lesson1_Notes.pdf', 'uploads/teacher_4/course_' || first_course_id || '/Lesson1_Notes.pdf', first_course_id, 4, 'PDF', 'Week 1 lecture notes', 1024576),
        ('Practice_Exercises.doc', 'uploads/teacher_4/course_' || first_course_id || '/Practice_Exercises.doc', first_course_id, 4, 'DOC', 'Weekly practice materials', 512000),
        ('Music_Theory_Basics.pdf', 'uploads/teacher_4/course_' || first_course_id || '/Music_Theory_Basics.pdf', first_course_id, 4, 'PDF', 'Basic theory concepts', 2048000);
END $$;

-- First, let's create some test students if they don't exist
-- Add role enum type if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('admin', 'teacher', 'student');
    END IF;
END $$;

-- Modify the users table insert to use proper role type (lowercase)
INSERT INTO users (name, email, password, role, active)
VALUES 
    ('John Smith', 'john.smith@example.com', '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q', 'student', true),
    ('Emma Davis', 'emma.davis@example.com', '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q', 'student', true),
    ('Michael Wilson', 'michael.wilson@example.com', '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q', 'student', true)
ON CONFLICT (email) DO NOTHING;

-- Now let's enroll these students in the courses
DO $$ 
DECLARE
    music_theory_id INTEGER;
    piano_basics_id INTEGER;
    guitar_master_id INTEGER;
    john_id INTEGER;
    emma_id INTEGER;
    michael_id INTEGER;
BEGIN
    -- Get course IDs
    SELECT id INTO music_theory_id FROM courses 
    WHERE teacher_id = 4 AND name = 'Music Theory 101';
    
    SELECT id INTO piano_basics_id FROM courses 
    WHERE teacher_id = 4 AND name = 'Piano Basics';
    
    SELECT id INTO guitar_master_id FROM courses 
    WHERE teacher_id = 4 AND name = 'Guitar Masterclass';
    
    -- Get student IDs
    SELECT id INTO john_id FROM users WHERE email = 'john.smith@example.com';
    SELECT id INTO emma_id FROM users WHERE email = 'emma.davis@example.com';
    SELECT id INTO michael_id FROM users WHERE email = 'michael.wilson@example.com';
    
    -- Create enrollments
    INSERT INTO enrollments (student_id, course_id)
    VALUES 
        -- John is enrolled in Music Theory and Piano
        (john_id, music_theory_id),
        (john_id, piano_basics_id),
        
        -- Emma is enrolled in all three courses
        (emma_id, music_theory_id),
        (emma_id, piano_basics_id),
        (emma_id, guitar_master_id),
        
        -- Michael is enrolled in Guitar Masterclass only
        (michael_id, guitar_master_id)
    ON CONFLICT (student_id, course_id) DO NOTHING;
        
END $$; 

-- Add schedule columns to courses table if they don't exist
ALTER TABLE courses 
ADD COLUMN IF NOT EXISTS day_of_week VARCHAR(10),
ADD COLUMN IF NOT EXISTS start_time TIME,
ADD COLUMN IF NOT EXISTS end_time TIME;

-- Update existing courses with schedule information
UPDATE courses 
SET day_of_week = CASE name
        WHEN 'Music Theory 101' THEN 'Monday'
        WHEN 'Piano Basics' THEN 'Wednesday'
        WHEN 'Guitar Masterclass' THEN 'Friday'
    END,
    start_time = CASE name
        WHEN 'Music Theory 101' THEN '09:00'::TIME
        WHEN 'Piano Basics' THEN '14:00'::TIME
        WHEN 'Guitar Masterclass' THEN '16:00'::TIME
    END,
    end_time = CASE name
        WHEN 'Music Theory 101' THEN '10:30'::TIME
        WHEN 'Piano Basics' THEN '15:30'::TIME
        WHEN 'Guitar Masterclass' THEN '17:30'::TIME
    END
WHERE teacher_id = 4 
AND name IN ('Music Theory 101', 'Piano Basics', 'Guitar Masterclass');