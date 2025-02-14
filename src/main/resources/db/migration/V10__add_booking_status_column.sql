-- Drop existing status column if exists
ALTER TABLE bookings DROP COLUMN IF EXISTS status;

-- Create booking status enum if not exists
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'booking_status') THEN
        CREATE TYPE booking_status AS ENUM ('BOOKED', 'CANCELLED');
    END IF;
END $$;

-- Add new status column with proper type
ALTER TABLE bookings 
ADD COLUMN status booking_status;

-- Set default value for existing records
UPDATE bookings 
SET status = 'BOOKED'::booking_status;

-- Add default constraint for new records
ALTER TABLE bookings
ALTER COLUMN status SET DEFAULT 'BOOKED'::booking_status,
ALTER COLUMN status SET NOT NULL; 