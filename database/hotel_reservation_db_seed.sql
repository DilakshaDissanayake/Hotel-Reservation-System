USE hotel_reservation_db;

INSERT INTO users (username, password_hash, role)
VALUES
('admin', '$2a$10$QxGmKJ9fXlE5F4jJ9WmE6u5f5bA9d3n6qG7m1wqO4xM9y7o9yYQ6a', 'ADMIN'),
('reception', '$2a$10$w7bOaRzJ8m7n3p0W1uM9qO9Z1x1Wv8Yl7Wq1uZbP9aG7HqWcQm1oC', 'RECEPTIONIST')
ON DUPLICATE KEY UPDATE username = username;


INSERT INTO rooms (room_number, room_type, rate_per_night, status, description)
VALUES
('S101', 'SINGLE', 8000.00, 'AVAILABLE', 'Single room with basic facilities'),
('S102', 'SINGLE', 8000.00, 'AVAILABLE', 'Single room with WiFi'),
('D201', 'DOUBLE', 12000.00, 'AVAILABLE', 'Double room with WiFi'),
('DL301', 'DELUXE', 18000.00, 'AVAILABLE', 'Deluxe room with Sea View'),
('SU401', 'SUITE', 25000.00, 'AVAILABLE', 'Suite room with premium facilities')
ON DUPLICATE KEY UPDATE room_number = room_number;

INSERT INTO facilities (name, category, description)
VALUES
('Breakfast Included', 'FOOD', 'Free breakfast for guests'),
('Dinner Included', 'FOOD', 'Dinner service'),
('Free WiFi', 'AMENITY', 'Unlimited WiFi'),
('Air Conditioner', 'AMENITY', 'AC available in the room'),
('Sea View', 'AMENITY', 'Ocean/Sea view room'),
('Airport Pickup', 'SERVICE', 'Transport service from airport')
ON DUPLICATE KEY UPDATE name = name;


INSERT INTO room_facilities (room_id, facility_id, extra_price_per_night)
VALUES
((SELECT id FROM rooms WHERE room_number='S101'), (SELECT id FROM facilities WHERE name='Free WiFi'), 0.00),
((SELECT id FROM rooms WHERE room_number='S101'), (SELECT id FROM facilities WHERE name='Breakfast Included'), 0.00)
ON DUPLICATE KEY UPDATE extra_price_per_night = VALUES(extra_price_per_night);

INSERT INTO room_facilities (room_id, facility_id, extra_price_per_night)
VALUES
((SELECT id FROM rooms WHERE room_number='DL301'), (SELECT id FROM facilities WHERE name='Sea View'), 1500.00),
((SELECT id FROM rooms WHERE room_number='DL301'), (SELECT id FROM facilities WHERE name='Air Conditioner'), 0.00)
ON DUPLICATE KEY UPDATE extra_price_per_night = VALUES(extra_price_per_night);

INSERT INTO room_facilities (room_id, facility_id, extra_price_per_night)
VALUES
((SELECT id FROM rooms WHERE room_number='SU401'), (SELECT id FROM facilities WHERE name='Dinner Included'), 2500.00),
((SELECT id FROM rooms WHERE room_number='SU401'), (SELECT id FROM facilities WHERE name='Airport Pickup'), 5000.00)
ON DUPLICATE KEY UPDATE extra_price_per_night = VALUES(extra_price_per_night);


INSERT INTO reservations (reservation_id, guest_count, address, contact_number, room_type, check_in_date, check_out_date, room_id)
VALUES
(1001, 2, 'Colombo, Sri Lanka', '0771234567', 'DELUXE', '2026-02-01', '2026-02-03',
 (SELECT id FROM rooms WHERE room_number='DL301')),
(1002, 3, 'Galle, Sri Lanka', '0719876543', 'SUITE', '2026-02-05', '2026-02-07',
 (SELECT id FROM rooms WHERE room_number='SU401'))
ON DUPLICATE KEY UPDATE reservation_id = reservation_id;


INSERT INTO reservation_guests (reservation_id, full_name, age, nic, passport_no, is_primary)
VALUES
(1001, 'Kamal Perera', 28, '200012345678', NULL, TRUE),
(1001, 'Nimali Perera', 26, '200145678901', NULL, FALSE);

INSERT INTO reservation_guests (reservation_id, full_name, age, nic, passport_no, is_primary)
VALUES
(1002, 'Sunil Fernando', 35, '198912345678', NULL, TRUE),
(1002, 'Dilani Fernando', 33, '199112345678', NULL, FALSE),
(1002, 'Sanu Fernando', 7, NULL, NULL, FALSE);


INSERT INTO bills (reservation_id, nights, rate_per_night, extras_total, discount_amount, sub_total, total)
VALUES
(1001, 2, 18000.00, 3000.00, 1000.00, 39000.00, 38000.00)
ON DUPLICATE KEY UPDATE reservation_id = reservation_id;


INSERT INTO bills (reservation_id, nights, rate_per_night, extras_total, discount_amount, sub_total, total)
VALUES
(1002, 2, 25000.00, 15000.00, 0.00, 65000.00, 65000.00)
ON DUPLICATE KEY UPDATE reservation_id = reservation_id;
