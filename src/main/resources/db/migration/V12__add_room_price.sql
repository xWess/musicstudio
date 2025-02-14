-- Add price column to rooms table if it doesn't exist
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'rooms' 
        AND column_name = 'price'
    ) THEN
        -- Add price column
        ALTER TABLE rooms 
        ADD COLUMN price DECIMAL(10,2) NOT NULL DEFAULT 50.00;

        -- Update existing rooms with sample prices
        UPDATE rooms SET price = 
            CASE 
                WHEN type = 'PRACTICE' THEN 50.00
                WHEN type = 'RECORDING' THEN 100.00
                WHEN type = 'PERFORMANCE' THEN 150.00
                ELSE 50.00
            END;
    END IF;
END $$; 