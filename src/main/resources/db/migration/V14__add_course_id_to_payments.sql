-- Add course_id column to payments table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'payments' 
        AND column_name = 'course_id'
    ) THEN
        -- Add course_id column
        ALTER TABLE payments 
        ADD COLUMN course_id INTEGER;

        -- Add foreign key constraint
        ALTER TABLE payments
        ADD CONSTRAINT fk_payment_course
        FOREIGN KEY (course_id) 
        REFERENCES courses(id);
    END IF;
END $$; 