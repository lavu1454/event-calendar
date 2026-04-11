-- Run this in HeidiSQL to set up the complete schema

CREATE DATABASE IF NOT EXISTS event_calendar_db;
USE event_calendar_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Events table with all columns
CREATE TABLE IF NOT EXISTS events (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    title         VARCHAR(100) NOT NULL,
    description   TEXT,
    start_time    DATETIME     NOT NULL,
    end_time      DATETIME     NOT NULL,
    category      VARCHAR(30),
    calendar_type VARCHAR(30),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Index for fast user+date range queries
CREATE INDEX IF NOT EXISTS idx_user_start_time ON events(user_id, start_time);

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token      VARCHAR(512) NOT NULL UNIQUE,
    user_id    BIGINT NOT NULL,
    expires_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Add missing columns to existing events table (safe to run even if columns exist)
ALTER TABLE events ADD COLUMN IF NOT EXISTS category VARCHAR(30);
ALTER TABLE events ADD COLUMN IF NOT EXISTS calendar_type VARCHAR(30);
