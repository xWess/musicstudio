DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS rooms;

CREATE TABLE rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(50) NOT NULL
);

CREATE TABLE schedules (
    id INT PRIMARY KEY AUTO_INCREMENT,
    day_of_week VARCHAR(10) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    course_id INT,
    room_id INT,
    booked_by INT,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (booked_by) REFERENCES users(id)
); 