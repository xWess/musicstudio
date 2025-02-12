-- Add sample courses for teacher with email 'ff'
DO $$ 
DECLARE
    teacher_id INTEGER;
BEGIN
    SELECT id INTO teacher_id FROM users WHERE email = 'ff';
    
    -- Add sample courses with just name and teacher_id
    INSERT INTO courses (name, teacher_id)
    VALUES 
        ('Music Theory 101', teacher_id),
        ('Piano Basics', teacher_id),
        ('Guitar Masterclass', teacher_id);
        
END $$; 