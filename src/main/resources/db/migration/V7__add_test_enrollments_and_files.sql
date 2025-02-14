-- Ajouter quelques cours de test s'ils n'existent pas déjà
INSERT INTO courses (id, teacher_id, name, schedule)
SELECT 1, 4, 'Piano Basics', 'Monday 10:00-12:00'
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1);

INSERT INTO courses (id, teacher_id, name, schedule)
SELECT 2, 4, 'Guitar Advanced', 'Tuesday 14:00-16:00'
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 2);

-- Ajouter des inscriptions pour l'étudiant de test (id=14)
INSERT INTO enrollments (student_id, course_id, start_date, end_date, created_at, status)
VALUES 
    (14, 1, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', CURRENT_TIMESTAMP, 'ACTIVE'),
    (14, 2, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', CURRENT_TIMESTAMP, 'ACTIVE');

-- Ajouter quelques fichiers de cours de test
INSERT INTO course_files (file_name, file_path, file_type, file_size, description, course_id, teacher_id, upload_date)
VALUES 
    ('Piano_Lesson1.pdf', '/uploads/Piano_Lesson1.pdf', 'pdf', 1024576, 'Introduction to Piano Basics', 1, 4, CURRENT_TIMESTAMP),
    ('Guitar_Notes.pdf', '/uploads/Guitar_Notes.pdf', 'pdf', 2048576, 'Advanced Guitar Techniques', 2, 4, CURRENT_TIMESTAMP),
    ('Piano_Practice.mp3', '/uploads/Piano_Practice.mp3', 'mp3', 5242880, 'Practice exercises for beginners', 1, 4, CURRENT_TIMESTAMP); 