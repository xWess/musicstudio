-- Update admin password using the hash that was verified to work in Test 2
UPDATE users 
SET password = '$2a$12$WlTdrnMueDH7tMeNug557O76RTAjyf58i5kHxjP7qVuFMTcvjvZg.'
WHERE email = 'admin@system.com'; 