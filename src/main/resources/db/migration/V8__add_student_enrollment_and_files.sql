-- S'assurer que le cours Piano Basics existe avec le bon professeur
INSERT INTO courses (id, teacher_id, name, schedule)
SELECT 1, 4, 'Piano Basics', 'Monday 10:00-12:00'
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1);

-- Inscrire l'étudiant (id=14) au cours Piano Basics s'il n'est pas déjà inscrit
INSERT INTO enrollments (student_id, course_id, start_date, end_date, created_at, status)
SELECT 14, 3, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', CURRENT_TIMESTAMP, 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM enrollments 
    WHERE student_id = 14 AND course_id = 3 AND status = 'ACTIVE'
);

-- Ajouter quelques fichiers de test pour le cours Piano Basics
INSERT INTO course_files (file_name, file_path, file_type, file_size, description, course_id, teacher_id, upload_date)
VALUES 
    ('Piano_Lesson1.pdf', '/uploads/Piano_Lesson1.pdf', 'pdf', 1024576, 'Introduction to Piano Basics', 1, 4, CURRENT_TIMESTAMP),
    ('Piano_Notes.pdf', '/uploads/Piano_Notes.pdf', 'pdf', 512000, 'Basic Piano Notes', 1, 4, CURRENT_TIMESTAMP),
    ('Piano_Practice.mp3', '/uploads/Piano_Practice.mp3', 'mp3', 3145728, 'Practice exercises for beginners', 1, 4, CURRENT_TIMESTAMP); 