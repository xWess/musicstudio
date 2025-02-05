-- Insert admin user first
INSERT INTO users (name, email, role, password, active) VALUES
(
    'Admin',
    'admin@musicstudio.com',
    'admin',
    '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q',
    true
);

-- Insert sample users
INSERT INTO users (name, email, role, password, active) VALUES
(
    'John Doe',
    'john.doe@example.com',
    'teacher',
    '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q',
    true
),
(
    'Jane Smith',
    'jane.smith@example.com',
    'student',
    '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q',
    true
),
(
    'Bob Artist',
    'bob.artist@example.com',
    'artist',
    '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q',
    true
);

-- Insert sample rooms
INSERT INTO rooms (location, capacity) VALUES
('Room 101', 20),
('Studio A', 5);

-- Insert sample courses
INSERT INTO courses (name, description, teacher_id, monthly_fee, schedule) VALUES
('Piano Basics', 'Learn piano fundamentals, music theory, and basic techniques', 
    (SELECT id FROM users WHERE email = 'john.doe@example.com'), 80.00, 'Monday, Wednesday 15:00-16:30'),
('Advanced Guitar', 'Advanced guitar techniques for experienced players',
    (SELECT id FROM users WHERE email = 'john.doe@example.com'), 90.00, 'Tuesday, Thursday 17:00-18:30'),
('Music Theory', 'Understanding the fundamentals of music',
    (SELECT id FROM users WHERE email = 'john.doe@example.com'), 60.00, 'Tuesday 10:00-11:30');

-- Add sample enrollment
INSERT INTO enrollments (student_id, course_id, end_date) VALUES
((SELECT id FROM users WHERE email = 'jane.smith@example.com'),
 (SELECT id FROM courses WHERE name = 'Piano Basics'),
 CURRENT_DATE + INTERVAL '6 months'); 