-- Add payment-related fields to courses
ALTER TABLE courses 
ADD COLUMN monthly_fee DECIMAL(10,2) NOT NULL DEFAULT 80.00;

-- Update existing courses with fees
UPDATE courses 
SET monthly_fee = CASE name
    WHEN 'Piano Basics' THEN 80.00
    WHEN 'Advanced Guitar' THEN 90.00
    ELSE 80.00
END;

-- Add payment reference to enrollments
ALTER TABLE enrollments 
ADD COLUMN payment_id INTEGER REFERENCES payments(id),
ADD COLUMN start_date DATE 