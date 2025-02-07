-- Add booking_date column to schedules table
ALTER TABLE schedules 
ADD COLUMN IF NOT EXISTS booking_date DATE;

-- Update existing records to use current date (optional)
UPDATE schedules 
SET booking_date = CURRENT_DATE 
WHERE booking_date IS NULL;

-- Make booking_date NOT NULL after updating existing records
ALTER TABLE schedules 
ALTER COLUMN booking_date SET NOT NULL;

-- Add index for better query performance
CREATE INDEX IF NOT EXISTS idx_schedules_booking_date 
ON schedules(booking_date); 