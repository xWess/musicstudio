-- Drop and recreate tables with proper structure
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS rooms;

CREATE TABLE rooms (
    id SERIAL PRIMARY KEY,
    location VARCHAR(50) NOT NULL,
    capacity INTEGER DEFAULT 10
);

CREATE TABLE schedules (
    id SERIAL PRIMARY KEY,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room_id INTEGER REFERENCES rooms(id),
    booked_by INTEGER REFERENCES users(id)
);

-- Add some sample data
INSERT INTO rooms (location, capacity) VALUES 
('Room 101', 10),
('Room 102', 10),
('Studio A', 5),
('Studio B', 5); 