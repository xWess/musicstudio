-- First check if payment_method column exists
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'payments' 
        AND column_name = 'payment_method'
    ) THEN
        -- Add payment_method column if it doesn't exist
        ALTER TABLE payments 
        ADD COLUMN payment_method VARCHAR(50);
        
        -- Set default value for existing payments
        UPDATE payments 
        SET payment_method = 'Credit Card' 
        WHERE payment_method IS NULL;
        
        -- Make the column not null
        ALTER TABLE payments 
        ALTER COLUMN payment_method SET NOT NULL;
    END IF;
END $$; 