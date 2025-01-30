-- V1__initial_schema.sql
-- Initial database schema for Music Studio application

-- Drop existing tables if they exist
DROP TABLE IF EXISTS spring_session_attributes CASCADE;
DROP TABLE IF EXISTS spring_session CASCADE;
DROP TABLE IF EXISTS student_courses CASCADE;
DROP TABLE IF EXISTS artists CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS schedules CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS rooms CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table (base table for Student, Teacher, Artist)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('student', 'teacher', 'artist'))
);

-- Create rooms table
CREATE TABLE rooms (
    id SERIAL PRIMARY KEY,
    location VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL
);

-- Create courses table
CREATE TABLE courses (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    teacher_id INTEGER REFERENCES users(id)
);

-- Create schedules table
CREATE TABLE schedules (
    id SERIAL PRIMARY KEY,
    day_of_week VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    course_id INTEGER REFERENCES courses(id),
    room_id INTEGER REFERENCES rooms(id),
    booked_by INTEGER REFERENCES users(id),
    CONSTRAINT valid_day_of_week CHECK (day_of_week IN 
        ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'))
);

-- Create enrollments table (junction table between students and courses)
CREATE TABLE enrollments (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES users(id),
    course_id INTEGER REFERENCES courses(id),
    UNIQUE(student_id, course_id)
);

-- Add indexes for common queries
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_courses_teacher ON courses(teacher_id);
CREATE INDEX idx_schedules_course ON schedules(course_id);
CREATE INDEX idx_schedules_room ON schedules(room_id);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);

-- Add some sample data for testing
INSERT INTO users (name, email, role) VALUES
('John Doe', 'john.doe@example.com', 'teacher'),
('Jane Smith', 'jane.smith@example.com', 'student'),
('Bob Artist', 'bob.artist@example.com', 'artist');

INSERT INTO rooms (location, capacity) VALUES
('Room 101', 20),
('Studio A', 5);

INSERT INTO courses (name, teacher_id) VALUES
('Piano Basics', 1),
('Advanced Guitar', 1);

-- Add sample schedule
INSERT INTO schedules (day_of_week, start_time, end_time, course_id, room_id) VALUES
('MONDAY', '09:00:00', '10:00:00', 1, 1);

-- Add sample enrollment
INSERT INTO enrollments (student_id, course_id) VALUES
(2, 1); 