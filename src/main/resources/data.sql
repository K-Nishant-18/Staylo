-- ============================================================
-- Staylo Seed Data
-- Admin, Warden, Property Owner, Student users pre-loaded
-- Passwords are BCrypt-encoded for "password123"
-- ============================================================

INSERT IGNORE INTO users (name, email, password, role, is_active, created_at) VALUES
('Super Admin',     'admin@staylo.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTa4dvKp06', 'ADMIN',          true, NOW()),
('Mr. Ramesh Warden', 'warden@staylo.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTa4dvKp06', 'WARDEN',         true, NOW()),
('Sharma Properties', 'owner@staylo.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTa4dvKp06', 'PROPERTY_OWNER', true, NOW()),
('Rahul Student',   'student@staylo.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LjTa4dvKp06', 'STUDENT',        true, NOW());

-- Hostel Rooms
INSERT IGNORE INTO hostel_rooms (room_number, hostel_block, floor, type, capacity, occupied, monthly_fee, has_attached_bathroom, has_ac, status) VALUES
('A-101', 'Block A', 1, 'SINGLE', 1, 0, 5000.00,  true,  true,  'AVAILABLE'),
('A-102', 'Block A', 1, 'DOUBLE', 2, 1, 3500.00,  true,  false, 'AVAILABLE'),
('A-103', 'Block A', 1, 'TRIPLE', 3, 0, 2500.00,  false, false, 'AVAILABLE'),
('B-201', 'Block B', 2, 'DOUBLE', 2, 0, 3800.00,  true,  true,  'AVAILABLE'),
('B-202', 'Block B', 2, 'SINGLE', 1, 1, 5500.00,  true,  true,  'FULL'),
('C-301', 'Block C', 3, 'TRIPLE', 3, 0, 2200.00,  false, false, 'MAINTENANCE');
