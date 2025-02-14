-- Add room_booking_id column to payments table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'payments' 
        AND column_name = 'room_booking_id'
    ) THEN
        -- Add room_booking_id column
        ALTER TABLE payments 
        ADD COLUMN room_booking_id INTEGER;

        -- Add foreign key constraint
        ALTER TABLE payments
        ADD CONSTRAINT fk_payment_room_booking
        FOREIGN KEY (room_booking_id) 
        REFERENCES bookings(id);
    END IF;
END $$; 