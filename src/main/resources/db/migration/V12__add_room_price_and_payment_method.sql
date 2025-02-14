-- Add price column to rooms table
ALTER TABLE rooms 
ADD COLUMN price DECIMAL(10,2) NOT NULL DEFAULT 50.00;

-- Add payment_method to payments table if it doesn't exist
ALTER TABLE payments 
ADD COLUMN IF NOT EXISTS payment_method VARCHAR(50);

-- Update existing rooms with sample prices
UPDATE rooms SET price = 
    CASE 
        WHEN type = 'PRACTICE' THEN 50.00
        WHEN type = 'RECORDING' THEN 100.00
        WHEN type = 'PERFORMANCE' THEN 150.00
        ELSE 50.00
    END; 