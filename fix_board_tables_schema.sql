-- =====================================================
-- Fix Board-related Tables Schema - Database Migration
-- =====================================================
-- Goal: Ensure all UUID columns use BINARY(16) to match JPA entities
-- Tables: boards, board_likes, comments, comment_likes
-- Run this on your MariaDB/MySQL database
-- =====================================================

USE upik;

-- ===== boards =====
DESCRIBE boards;
ALTER TABLE boards
    MODIFY COLUMN id BINARY(16) NOT NULL,
    MODIFY COLUMN user_id BINARY(16) NOT NULL;
DESCRIBE boards;

-- ===== board_likes =====
DESCRIBE board_likes;
ALTER TABLE board_likes
    MODIFY COLUMN id BINARY(16) NOT NULL,
    MODIFY COLUMN user_id BINARY(16) NOT NULL,
    MODIFY COLUMN board_id BINARY(16) NOT NULL;
DESCRIBE board_likes;

-- ===== comments =====
DESCRIBE comments;
ALTER TABLE comments
    MODIFY COLUMN id BINARY(16) NOT NULL,
    MODIFY COLUMN user_id BINARY(16) NOT NULL,
    MODIFY COLUMN board_id BINARY(16) NOT NULL,
    MODIFY COLUMN parent_id BINARY(16);
DESCRIBE comments;

-- ===== comment_likes =====
DESCRIBE comment_likes;
ALTER TABLE comment_likes
    MODIFY COLUMN id BINARY(16) NOT NULL,
    MODIFY COLUMN user_id BINARY(16) NOT NULL,
    MODIFY COLUMN comment_id BINARY(16) NOT NULL;
DESCRIBE comment_likes;

SELECT 'Board-related tables schema fix completed successfully!' AS status;
