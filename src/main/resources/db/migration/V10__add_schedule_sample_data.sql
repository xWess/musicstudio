-- First ensure we have some rooms
INSERT INTO rooms (id, location) VALUES 
(1, 'Room 101'),
(2, 'Room 102'),
(3, 'Studio A'),
(4, 'Studio B');

-- Add some sample schedules for existing courses
INSERT INTO schedules (day_of_week, start_time, end_time, course_id, room_id, booked_by) VALUES
('Monday', '09:00:00', '10:30:00', 1, 1, 1),
('Monday', '11:00:00', '12:30:00', 2, 2, 1),
('Tuesday', '14:00:00', '15:30:00', 1, 3, 2),
('Wednesday', '16:00:00', '17:30:00', 2, 4, 2),
('Thursday', '10:00:00', '11:30:00', 3, 1, 1),
('Friday', '13:00:00', '14:30:00', 3, 2, 2); 