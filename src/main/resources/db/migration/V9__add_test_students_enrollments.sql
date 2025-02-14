-- S'assurer que les cours existent pour l'enseignant (id=4)
INSERT INTO courses (id, teacher_id, name, schedule)
SELECT 3, 4, 'Guitar Basics', 'Monday 14:00-16:00'
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 3);

INSERT INTO courses (id, teacher_id, name, schedule)
SELECT 4, 4, 'Advanced Piano', 'Wednesday 10:00-12:00'
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 4);

-- Ajouter quelques étudiants de test s'ils n'existent pas déjà
INSERT INTO users (id, name, email, password, role)
SELECT 15, 'John Doe', 'john@test.com', '$2a$12$lY6s0zHFHsU.8Yb3lFZq/./bHzwwXmpXqFfd2a848jfTg69uF3/2O', 'student'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 15);

INSERT INTO users (id, name, email, password, role)
SELECT 16, 'Jane Smith', 'jane@test.com', '$2a$12$lY6s0zHFHsU.8Yb3lFZq/./bHzwwXmpXqFfd2a848jfTg69uF3/2O', 'student'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE id = 16);

-- Inscrire les étudiants aux cours du professeur (id=4)
INSERT INTO enrollments (student_id, course_id, start_date, end_date, created_at, status)
VALUES 
    (15, 3, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', CURRENT_TIMESTAMP, 'ACTIVE'),
    (16, 3, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', CURRENT_TIMESTAMP, 'ACTIVE'),
    (15, 4, CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months', CURRENT_TIMESTAMP, 'ACTIVE'); 