-- First, add the new columns to courses table
ALTER TABLE courses
ADD COLUMN description TEXT,
ADD COLUMN monthly_fee DECIMAL(10,2) DEFAULT 80.00,
ADD COLUMN schedule VARCHAR(200);

-- Update existing courses with more details
UPDATE courses 
SET description = CASE id
        WHEN 1 THEN 'Learn piano from basics to intermediate level. Covers theory and practice.'
        WHEN 2 THEN 'Advanced guitar techniques for experienced players.'
    END,
    monthly_fee = CASE id
        WHEN 1 THEN 80.00
        WHEN 2 THEN 90.00
    END,
    schedule = CASE id
        WHEN 1 THEN 'Monday, Wednesday 15:00-16:30'
        WHEN 2 THEN 'Tuesday, Thursday 17:00-18:30'
    END;

-- Add more sample courses
INSERT INTO courses (name, teacher_id, description, monthly_fee, schedule) VALUES
('Vocal Training', 1, 'Develop your singing voice with professional techniques', 75.00, 'Monday, Friday 14:00-15:30'),
('Drum Basics', 1, 'Introduction to drums and rhythm', 85.00, 'Wednesday, Friday 16:00-17:30'),
('Music Theory', 1, 'Understanding the fundamentals of music', 60.00, 'Tuesday 10:00-11:30'); 