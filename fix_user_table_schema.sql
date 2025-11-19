-- =====================================================
-- Fix User Table Schema - Database Migration
-- =====================================================
-- Issue: 'id' column is too short to store BINARY(16) UUID
-- Error: "Data too long for column 'id' at row 1"
--
-- This script fixes the user table schema to match the JPA entity definition
-- Run this on your MariaDB/MySQL database
-- =====================================================

USE upik;

-- Check current table structure
DESCRIBE user;

-- Fix: Change 'id' column to BINARY(16) to properly store UUID
ALTER TABLE user MODIFY COLUMN id BINARY(16) NOT NULL;

-- Verify the change
DESCRIBE user;

-- Expected output for 'id' column:
-- Field: id
-- Type: binary(16)
-- Null: NO
-- Key: PRI

SELECT 'User table schema fix completed successfully!' AS status;
