-- First, let's check if the user exists and create if not
DO $$ 
DECLARE
    teacher_id INTEGER;
BEGIN
    -- Try to get existing teacher id
    SELECT id INTO teacher_id FROM users WHERE email = 'ff';
    
    -- If teacher doesn't exist, create them
    IF teacher_id IS NULL THEN
        INSERT INTO users (name, email, password, role)
        VALUES ('Test Teacher', 'ff', '$2a$12$SYCrerzqMr.jo4VWWqP.XuBqGrndXja/S6/zHZ/dkg4HmBX4mneDa', 'TEACHER')
        RETURNING id INTO teacher_id;
    END IF;
    
    -- Add sample courses
    INSERT INTO courses (name, teacher_id)
    VALUES 
        ('Music Theory 101', teacher_id),
        ('Piano Basics', teacher_id),
        ('Guitar Masterclass', teacher_id);
        
END $$; 