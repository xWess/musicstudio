INSERT INTO users (name, email, role, password, active)
VALUES (
    'Admin',
    'admin@musicstudio.com',
    'admin',
    -- This creates a password hash for 'admin123'
    '$2a$12$LQV3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqyPredOkTEU/3q',
    true
)
ON CONFLICT (email) DO NOTHING; 