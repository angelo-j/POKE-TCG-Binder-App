-- test-schema.sql

BEGIN TRANSACTION;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS binder_cards CASCADE;
DROP TABLE IF EXISTS binders CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL, -- Changed from hashed_password
    role VARCHAR(50) NOT NULL,
    money NUMERIC(10, 2) DEFAULT 100.00 NOT NULL
);

-- Create binder table
CREATE TABLE binders (
    binder_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create binder_cards table
CREATE TABLE binder_cards (
    id SERIAL PRIMARY KEY,
    binder_id INTEGER NOT NULL,
    card_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    small_image_url TEXT NOT NULL,
    large_image_url TEXT NOT NULL,
    price NUMERIC(10, 2) DEFAULT 0.00,
    quantity INT DEFAULT 1,
    FOREIGN KEY (binder_id) REFERENCES binders(binder_id) ON DELETE CASCADE
);

COMMIT TRANSACTION;
