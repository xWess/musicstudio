-- Create booking status enum if not exists
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'booking_status') THEN
        CREATE TYPE booking_status AS ENUM ('BOOKED', 'CANCELLED');
    END IF;
END $$;

-- Create bookings table if it doesn't exist
CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    room_id INTEGER NOT NULL REFERENCES rooms(id),
    user_id INTEGER NOT NULL REFERENCES users(id),
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status booking_status DEFAULT 'BOOKED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Add constraint to ensure end_time is after start_time
    CONSTRAINT check_booking_time_order CHECK (end_time > start_time),
    
    -- Add constraint to prevent overlapping bookings for same room
    CONSTRAINT no_overlapping_bookings UNIQUE (room_id, booking_date, start_time, end_time),
    
    -- Add foreign key constraints
    CONSTRAINT fk_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index for faster booking queries
CREATE INDEX IF NOT EXISTS idx_bookings_search 
ON bookings(booking_date, room_id, status);

-- Create index for user bookings lookup
CREATE INDEX IF NOT EXISTS idx_user_bookings
ON bookings(user_id, booking_date); 