-- Initial database schema creation
-- This script creates the basic tables for the poll system

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    url_image_profile VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Polls table
CREATE TABLE IF NOT EXISTS polls (
    id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- Poll options table
CREATE TABLE IF NOT EXISTS poll_options (
    id VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    poll_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (poll_id) REFERENCES polls(id) ON DELETE CASCADE
);

-- Votes table
CREATE TABLE IF NOT EXISTS votes (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    poll_option_id VARCHAR(255) NOT NULL,
    poll_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (poll_option_id) REFERENCES poll_options(id),
    FOREIGN KEY (poll_id) REFERENCES polls(id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_polls_status ON polls(status);
CREATE INDEX IF NOT EXISTS idx_polls_owner_id ON polls(owner_id);
CREATE INDEX IF NOT EXISTS idx_poll_options_poll_id ON poll_options(poll_id);
CREATE INDEX IF NOT EXISTS idx_votes_poll_id ON votes(poll_id);
CREATE INDEX IF NOT EXISTS idx_votes_user_id ON votes(user_id);
CREATE INDEX IF NOT EXISTS idx_votes_poll_option_id ON votes(poll_option_id);


