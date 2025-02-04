-- Enhance courses table with additional fields
ALTER TABLE courses
ADD COLUMN description TEXT,
ADD COLUMN monthly_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
ADD COLUMN max_students INTEGER NOT NULL DEFAULT 20,
ADD COLUMN active BOOLEAN NOT NULL DEFAULT true;

-- Add payment reference to enrollments
ALTER TABLE enrollments
ADD COLUMN start_date DATE NOT NULL DEFAULT CURRENT_DATE,
ADD COLUMN end_date DATE NOT NULL,
ADD COLUMN payment_id INTEGER REFERENCES payments(id),
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED'));

-- Add indexes for performance
CREATE INDEX idx_payments_user ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_enrollments_payment ON enrollments(payment_id);
CREATE INDEX idx_enrollments_dates ON enrollments(start_date, end_date);

-- Update sample course data with new fields
UPDATE courses SET
    description = CASE id
        WHEN 1 THEN 'Learn piano fundamentals, music theory, and basic techniques'
        WHEN 2 THEN 'Advanced guitar techniques, improvisation, and performance skills'
    END,
    monthly_fee = CASE id
        WHEN 1 THEN 80.00  -- Piano Basics
        WHEN 2 THEN 90.00  -- Advanced Guitar
    END; 