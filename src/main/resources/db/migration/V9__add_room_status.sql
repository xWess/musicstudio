-- Add status enum type if not exists
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'room_status') THEN
        CREATE TYPE room_status AS ENUM ('AVAILABLE', 'BOOKED', 'MAINTENANCE');
    END IF;
END $$;

-- Add status column to rooms table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'rooms' 
        AND column_name = 'status'
    ) THEN
        ALTER TABLE rooms 
        ADD COLUMN status room_status DEFAULT 'AVAILABLE';
    END IF;
END $$;

-- Update existing rooms to have a default status
UPDATE rooms 
SET status = 'AVAILABLE' 
WHERE status IS NULL; 