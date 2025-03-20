-- test-data.sql
BEGIN TRANSACTION;

-- Ensure tables start fresh
TRUNCATE TABLE binder_cards RESTART IDENTITY CASCADE;
TRUNCATE TABLE binders RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- Reset binder_id sequence
ALTER SEQUENCE binders_binder_id_seq RESTART WITH 1;

-- Insert test users
INSERT INTO users (username, password_hash, role, money) VALUES
('test_user1', 'hashedpassword', 'ROLE_USER', 100.00),
('test_user2', 'hashedpassword', 'ROLE_USER', 100.00),
('test_user3', 'hashedpassword', 'ROLE_USER', 100.00);

-- Insert test binders
INSERT INTO binders (name, user_id) VALUES
('Binder 1', 1),
('Binder 2', 2),
('Binder 3', 3);

-- Insert test binder cards
INSERT INTO binder_cards (binder_id, card_id, name, small_image_url, large_image_url, price, quantity) VALUES
(1, 'xy1-1', 'Pikachu', 'small_image_1', 'large_image_1', 1.50, 2),
(1, 'xy1-2', 'Charizard', 'small_image_2', 'large_image_2', 5.00, 1),
(2, 'xy1-3', 'Squirtle', 'small_image_3', 'large_image_3', 0.75, 3);

COMMIT TRANSACTION;
