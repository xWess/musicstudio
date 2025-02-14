-- Add room types enum if not exists
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'room_type') THEN
        CREATE TYPE room_type AS ENUM ('PRACTICE', 'RECORDING', 'PERFORMANCE');
    END IF;
END $$;

-- Add type column to rooms if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'rooms' 
        AND column_name = 'type'
    ) THEN
        ALTER TABLE rooms ADD COLUMN type room_type NOT NULL DEFAULT 'PRACTICE'::room_type;
    END IF;
END $$;

-- Add date column to schedules if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'schedules' 
        AND column_name = 'date'
    ) THEN
        ALTER TABLE schedules ADD COLUMN date DATE;
    END IF;
END $$;

-- Add booked_by and status columns to schedules if they don't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'schedules' 
        AND column_name = 'booked_by'
    ) THEN
        ALTER TABLE schedules ADD COLUMN booked_by INTEGER REFERENCES users(id);
    END IF;
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'schedules' 
        AND column_name = 'status'
    ) THEN
        ALTER TABLE schedules ADD COLUMN status VARCHAR(20);
    END IF;
END $$;

-- Update existing rooms with types
UPDATE rooms 
SET type = 'PRACTICE'::room_type 
WHERE type IS NULL;

-- Insert sample rooms only if they don't exist
INSERT INTO rooms (location, capacity, type)
SELECT r.location, r.capacity, r.type::room_type
FROM (VALUES
    ('Room A101', 4, 'PRACTICE'),
    ('Room A102', 4, 'PRACTICE'),
    ('Room B201', 8, 'RECORDING'),
    ('Room B202', 8, 'RECORDING'),
    ('Hall C301', 20, 'PERFORMANCE')
) AS r(location, capacity, type)
WHERE NOT EXISTS (
    SELECT 1 FROM rooms 
    WHERE location = r.location
);

-- Create index for faster room searches if it doesn't exist
CREATE INDEX IF NOT EXISTS idx_room_bookings 
ON schedules(room_id, date, status);

-- Create or replace trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Drop and recreate trigger
DROP TRIGGER IF EXISTS update_schedules_updated_at ON schedules;
CREATE TRIGGER update_schedules_updated_at
    BEFORE UPDATE ON schedules
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column(); 