-- Add sample course files for teacher with email 'ff'
DO $$ 
DECLARE
    teacher_id INTEGER;
    course_id INTEGER;
BEGIN
    -- Get the teacher ID
    SELECT id INTO teacher_id FROM users WHERE email = 'ff';
    
    -- Get one of the teacher's course IDs
    SELECT id INTO course_id FROM courses WHERE teacher_id = teacher_id LIMIT 1;
    
    -- Add sample course files
    INSERT INTO course_files (file_name, file_path, course_id, teacher_id, file_type, description, file_size)
    VALUES 
        ('Lesson1_Notes.pdf', 'uploads/course_1/Lesson1_Notes.pdf', course_id, teacher_id, 'PDF', 'Week 1 lecture notes', 1024576),
        ('Practice_Exercises.doc', 'uploads/course_1/Practice_Exercises.doc', course_id, teacher_id, 'DOC', 'Weekly practice materials', 512000),
        ('Music_Theory_Basics.pdf', 'uploads/course_1/Music_Theory_Basics.pdf', course_id, teacher_id, 'PDF', 'Basic theory concepts', 2048000);
        
END $$; 