-- V1__initial_schema.sql
-- Initial database schema for Music Studio application

-- Create users table (base table for Student, Teacher, Artist)
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    last_login TIMESTAMP,
    reset_token VARCHAR(36),
    reset_token_expiry TIMESTAMP,
    CONSTRAINT users_role_check CHECK (role IN ('student', 'teacher', 'artist', 'admin'))
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
    description TEXT,
    teacher_id INTEGER REFERENCES users(id),
    monthly_fee DECIMAL(10,2) NOT NULL DEFAULT 80.00,
    max_students INTEGER NOT NULL DEFAULT 20,
    active BOOLEAN NOT NULL DEFAULT true,
    schedule VARCHAR(200)
);

-- Create payments table
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- Create enrollments table (junction table between students and courses)
CREATE TABLE enrollments (
    id SERIAL PRIMARY KEY,
    student_id INTEGER REFERENCES users(id),
    course_id INTEGER REFERENCES courses(id),
    start_date DATE NOT NULL DEFAULT CURRENT_DATE,
    end_date DATE NOT NULL,
    payment_id INTEGER REFERENCES payments(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT enrollments_status_check CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED')),
    UNIQUE(student_id, course_id)
);

-- Create indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_courses_teacher ON courses(teacher_id);
CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_enrollments_payment ON enrollments(payment_id);
CREATE INDEX idx_enrollments_dates ON enrollments(start_date, end_date); 