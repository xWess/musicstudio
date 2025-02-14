ALTER TABLE payments 
ADD COLUMN IF NOT EXISTS payment_method VARCHAR(50),
ADD COLUMN IF NOT EXISTS room_booking_id INTEGER,
ADD CONSTRAINT fk_room_booking 
    FOREIGN KEY (room_booking_id) 
    REFERENCES bookings(id); 