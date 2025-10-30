-- Notification 테이블 생성 SQL

CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'VOTE_ENDED, GUIDE_CREATED, QUESTION_REPLY 등',
    title VARCHAR(255) NOT NULL,
    content TEXT,
    reference_id BINARY(16) COMMENT '참조하는 엔티티의 ID (투표, 가이드, 질문 등)',
    `read` BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_read (user_id, `read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
