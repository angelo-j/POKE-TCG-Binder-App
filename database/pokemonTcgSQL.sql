-- TCG Binder App SQL
BEGIN TRANSACTION;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS binder_cards CASCADE;
DROP TABLE IF EXISTS binder CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(50) NOT NULL,
    money NUMERIC(10, 2) DEFAULT 100.00 NOT NULL
);

-- Create binder table
CREATE TABLE binder (
    binder_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create binder_cards table with quantity column and unique ID
CREATE TABLE binder_cards (
    id SERIAL PRIMARY KEY,                                                  -- Unique ID for each entry
    binder_id INTEGER NOT NULL,                                             -- Foreign key to binder
    card_id VARCHAR(50) NOT NULL,                                           -- Card ID
    name VARCHAR(100) NOT NULL,                                             -- Card name
    small_image_url TEXT NOT NULL,                                          -- Small image URL
    large_image_url TEXT NOT NULL,                                          -- Large image URL
    price NUMERIC(10, 2) DEFAULT 0.00,                                      -- Price
    quantity INT DEFAULT 1,                                                 -- Quantity of cards
    FOREIGN KEY (binder_id) REFERENCES binder(binder_id) ON DELETE CASCADE  -- Foreign key constraint
);

COMMIT TRANSACTION;
