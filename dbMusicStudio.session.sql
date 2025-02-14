-- Ajouter la colonne description à la table courses si elle n'existe pas
ALTER TABLE courses 
ADD COLUMN IF NOT EXISTS description TEXT;

-- Table pour stocker les fichiers de cours
CREATE TABLE IF NOT EXISTS course_files (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size BIGINT,
    description TEXT,
    course_id INTEGER REFERENCES courses(id) ON DELETE CASCADE,
    teacher_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(file_path)
);

-- Index pour améliorer les performances des recherches
CREATE INDEX IF NOT EXISTS idx_course_files_course_id ON course_files(course_id);
CREATE INDEX IF NOT EXISTS idx_course_files_teacher_id ON course_files(teacher_id);

-- Ajouter des cours pour l'enseignant ID 4
INSERT INTO courses (name, description, teacher_id)
VALUES 
    ('Guitar Basics', 'Introduction to guitar playing', 4),
    ('Piano Fundamentals', 'Learn piano from scratch', 4),
    ('Music Theory', 'Understanding music theory', 4);

-- Vérifier les cours ajoutés
SELECT * FROM courses WHERE teacher_id = 4;

-- Vérifier que les dossiers nécessaires existent
-- Note: Cette commande doit être exécutée au niveau du système de fichiers, pas dans PostgreSQL
-- mkdir -p uploads/course_files

-- Accorder les permissions nécessaires
GRANT ALL PRIVILEGES ON TABLE course_files TO postgres;
GRANT USAGE, SELECT ON SEQUENCE course_files_id_seq TO postgres;
