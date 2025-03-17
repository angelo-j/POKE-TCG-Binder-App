BEGIN TRANSACTION;

-- Insert test user
INSERT INTO users (username, password_hash, role, money) VALUES ('user3','password', 'ROLE_USER', 75.00);

COMMIT TRANSACTION;
