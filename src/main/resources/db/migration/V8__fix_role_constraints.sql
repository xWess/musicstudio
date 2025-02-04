-- Drop existing role constraint
ALTER TABLE users 
DROP CONSTRAINT IF EXISTS users_role_check;

-- Add new constraint with all roles in lowercase
ALTER TABLE users 
ADD CONSTRAINT users_role_check 
CHECK (role IN ('student', 'teacher', 'artist', 'admin'));

-- Update any existing uppercase roles to lowercase
UPDATE users 
SET role = LOWER(role) 
WHERE role = ANY (ARRAY['STUDENT', 'TEACHER', 'ARTIST', 'ADMIN']); 