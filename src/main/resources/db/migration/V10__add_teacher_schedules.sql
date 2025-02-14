-- Ajouter des horaires pour les cours de l'enseignant (id=4)
INSERT INTO schedules (day_of_week, start_time, end_time, course_id, room_id, date, status)
VALUES 
    -- Pour le cours Guitar Basics (id=3)
    ('MONDAY', 
     '14:00:00', 
     '16:00:00', 
     3, 
     1,
     CURRENT_DATE,
     'ACTIVE'),
     
    -- Pour le cours Advanced Piano (id=4)
    ('WEDNESDAY', 
     '10:00:00', 
     '12:00:00', 
     4, 
     2,
     CURRENT_DATE,
     'ACTIVE'); 