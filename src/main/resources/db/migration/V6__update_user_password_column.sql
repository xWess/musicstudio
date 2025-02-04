-- Update the password column to be NOT NULL and with proper length for BCrypt hashes
ALTER TABLE users 
ALTER COLUMN password TYPE VARCHAR(60),
ALTER COLUMN password SET NOT NULL; 