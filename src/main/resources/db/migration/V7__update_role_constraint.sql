-- First, drop the existing check constraint
ALTER TABLE users 
DROP CONSTRAINT IF EXISTS users_role_check;

-- Add the new check constraint including ADMIN
ALTER TABLE users 
ADD CONSTRAINT users_role_check 
CHECK (role IN ('STUDENT', 'TEACHER', 'ARTIST', 'ADMIN'));

-- Then insert the admin user
INSERT INTO users (name, email, role, password, active)
VALUES (
    'Admin',
    'admin@musicstudio.com',
    'ADMIN',
    '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q',
    true
)
ON CONFLICT (email) DO NOTHING; 