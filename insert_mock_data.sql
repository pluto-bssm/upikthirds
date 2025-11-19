-- =====================================================
-- Mock Data Insertion Script for UPIK Database
-- =====================================================
-- This script inserts 10+ records into each table
-- Run this after database schema is created
-- =====================================================

USE upik;

-- Disable foreign key checks for easier insertion
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. User Table (기준 테이블)
-- =====================================================
INSERT INTO user (id, role, username, name, email, created_at, recent_date, dollar, won, streak_count) VALUES
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440001', '-', '')), 'ADMIN', 'admin', '관리자', 'admin@upik.com', NOW(), NOW(), 100.5, 50000, 15),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), 'USER', 'user001', '김철수', 'kim001@example.com', NOW(), NOW(), 50.0, 25000, 10),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), 'USER', 'user002', '이영희', 'lee002@example.com', NOW(), NOW(), 75.3, 37500, 12),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), 'USER', 'user003', '박민수', 'park003@example.com', NOW(), NOW(), 30.0, 15000, 5),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), 'USER', 'user004', '최지은', 'choi004@example.com', NOW(), NOW(), 45.7, 22800, 8),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), 'USER', 'user005', '정대현', 'jung005@example.com', NOW(), NOW(), 60.0, 30000, 11),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), 'USER', 'user006', '한소희', 'han006@example.com', NOW(), NOW(), 25.5, 12750, 4),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), 'USER', 'user007', '윤서준', 'yoon007@example.com', NOW(), NOW(), 80.2, 40100, 13),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), 'USER', 'user008', '임하늘', 'lim008@example.com', NOW(), NOW(), 55.0, 27500, 9),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), 'USER', 'user009', '강민지', 'kang009@example.com', NOW(), NOW(), 40.0, 20000, 7),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), 'USER', 'user010', '송준호', 'song010@example.com', NOW(), NOW(), 90.5, 45250, 14),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), 'USER', 'user011', '배수지', 'bae011@example.com', NOW(), NOW(), 35.0, 17500, 6),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), 'USER', 'user012', '신동엽', 'shin012@example.com', NOW(), NOW(), 70.0, 35000, 12);

-- =====================================================
-- 2. RefreshToken Table
-- =====================================================
INSERT INTO refresh_token (user_id, token, role, expiry_date) VALUES
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440001', '-', '')), 'refresh_token_admin_001', 'ADMIN', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), 'refresh_token_user_002', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), 'refresh_token_user_003', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), 'refresh_token_user_004', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), 'refresh_token_user_005', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), 'refresh_token_user_006', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), 'refresh_token_user_007', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), 'refresh_token_user_008', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), 'refresh_token_user_009', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), 'refresh_token_user_010', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), 'refresh_token_user_011', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), 'refresh_token_user_012', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY)),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), 'refresh_token_user_013', 'USER', DATE_ADD(NOW(), INTERVAL 30 DAY));

-- =====================================================
-- 3. AccessTokenCode Table
-- =====================================================
INSERT INTO AccessTokenCode (code, accessToken) VALUES
('auth_code_001', 'access_token_001'),
('auth_code_002', 'access_token_002'),
('auth_code_003', 'access_token_003'),
('auth_code_004', 'access_token_004'),
('auth_code_005', 'access_token_005'),
('auth_code_006', 'access_token_006'),
('auth_code_007', 'access_token_007'),
('auth_code_008', 'access_token_008'),
('auth_code_009', 'access_token_009'),
('auth_code_010', 'access_token_010'),
('auth_code_011', 'access_token_011'),
('auth_code_012', 'access_token_012'),
('auth_code_013', 'access_token_013');

-- =====================================================
-- 4. RefreshTokenCode Table
-- =====================================================
INSERT INTO RefreshTokenCode (code, refreshToken) VALUES
('refresh_code_001', 'refresh_token_code_001'),
('refresh_code_002', 'refresh_token_code_002'),
('refresh_code_003', 'refresh_token_code_003'),
('refresh_code_004', 'refresh_token_code_004'),
('refresh_code_005', 'refresh_token_code_005'),
('refresh_code_006', 'refresh_token_code_006'),
('refresh_code_007', 'refresh_token_code_007'),
('refresh_code_008', 'refresh_token_code_008'),
('refresh_code_009', 'refresh_token_code_009'),
('refresh_code_010', 'refresh_token_code_010'),
('refresh_code_011', 'refresh_token_code_011'),
('refresh_code_012', 'refresh_token_code_012'),
('refresh_code_013', 'refresh_token_code_013');

-- =====================================================
-- 5. AIUsageQuota Table
-- =====================================================
INSERT INTO aiusage_quota (user_id, usage_count, last_reset_date, updated_at) VALUES
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440001', '-', '')), 5, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), 3, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), 7, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), 2, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), 4, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), 6, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), 1, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), 8, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), 5, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), 3, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), 9, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), 2, CURDATE(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), 4, CURDATE(), NOW());

-- =====================================================
-- 6. Vote Table
-- =====================================================
INSERT INTO vote (id, creator_id, question, category, status, finished_at, closure_type, participant_threshold) VALUES
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440001', '-', '')), '가장 좋아하는 프로그래밍 언어는?', '기술', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), '점심 메뉴 추천', '음식', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'CUSTOM_DAYS', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), '주말 여행지 추천', '여행', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), '영화 추천', '엔터테인먼트', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'PARTICIPANT_COUNT', 50),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), '책 추천', '독서', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), '운동 종류 추천', '건강', 'CLOSED', CURDATE(), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), '카페 추천', '음식', 'CLOSED', CURDATE(), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), '게임 추천', '게임', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), '노래 추천', '음악', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), '쇼핑몰 추천', '쇼핑', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), '스터디 카페 추천', '학습', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), '강아지 품종 추천', '반려동물', 'OPEN', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'DEFAULT', NULL),
(UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), '노트북 브랜드 추천', '기술', 'CLOSED', CURDATE(), 'DEFAULT', NULL);

-- =====================================================
-- 7. Option Table
-- =====================================================
INSERT INTO `option` (id, vote_id, content) VALUES
-- Vote 1 options
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), 'Java'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), 'Python'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), 'JavaScript'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), 'C++'),
-- Vote 2 options
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), '한식'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), '중식'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), '일식'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), '양식'),
-- Vote 3 options
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), '부산'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), '제주도'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), '강릉'),
-- Vote 4 options
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), '액션 영화'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), '로맨스 영화'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440014', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), 'SF 영화'),
-- Vote 5 options
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440015', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), '소설'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440016', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), '자기계발서'),
(UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440017', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), '역사책');

-- =====================================================
-- 8. VoteResponse Table
-- =====================================================
INSERT INTO vote_response (id, user_id, vote_id, option_id, created_at) VALUES
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440001', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440002', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440003', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440005', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440006', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440009', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440010', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440012', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440013', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440015', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440016', '-', '')), CURDATE()),
(UNHEX(REPLACE('880e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('770e8400-e29b-41d4-a716-446655440017', '-', '')), CURDATE());

-- =====================================================
-- 9. Guide Table
-- =====================================================
-- Note: Guide와 Vote는 1:1 관계이므로 각 vote_id는 한 번만 사용됩니다
-- CLOSED 상태의 투표(vote 6, 7, 13)에만 가이드를 생성합니다
INSERT INTO guide (id, vote_id, title, content, created_at, category, guide_type, revote_count, `like`) VALUES
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440006', '-', '')), '운동 가이드', '건강한 운동 습관을 위한 종합 가이드입니다. 주 3회 이상 규칙적인 운동이 중요합니다.', CURDATE(), '건강', 'COMPREHENSIVE', 5, 25),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440007', '-', '')), '카페 방문 가이드', '서울 핫플레이스 카페 탐방 가이드. 분위기 좋은 카페 10곳을 소개합니다.', CURDATE(), '음식', 'REVIEW', 3, 18),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440013', '-', '')), '노트북 구매 가이드', '개발자를 위한 노트북 선택 가이드. CPU, RAM, SSD 용량을 고려하세요.', CURDATE(), '기술', 'BUYING_GUIDE', 7, 42),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), '프로그래밍 언어 선택 가이드', '초보자를 위한 프로그래밍 언어 추천. Python으로 시작하는 것을 추천합니다.', CURDATE(), '기술', 'BEGINNER', 2, 15),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), '점심 메뉴 추천 가이드', '직장인을 위한 점심 메뉴 추천. 영양 균형을 고려한 식단을 소개합니다.', CURDATE(), '음식', 'RECOMMENDATION', 4, 30),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), '주말 여행지 가이드', '가족과 함께 떠나는 주말 여행지 추천. 부산, 제주도, 강릉을 비교합니다.', CURDATE(), '여행', 'COMPARISON', 6, 38),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), '영화 추천 가이드', '주말에 볼만한 영화 추천. 장르별 베스트 영화를 소개합니다.', CURDATE(), '엔터테인먼트', 'HOW_TO', 8, 55),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), '독서 가이드', '독서 습관 만들기. 하루 30분 독서로 시작하는 방법을 소개합니다.', CURDATE(), '독서', 'TIPS', 1, 12),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440008', '-', '')), '게임 추천 가이드', '2024년 꼭 해봐야 할 게임 10선. RPG부터 전략 게임까지 다양하게 소개합니다.', CURDATE(), '게임', 'SPECIALIZED', 9, 48),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440009', '-', '')), '음악 추천 가이드', '기분 전환을 위한 음악 추천. 장르별 명곡 플레이리스트를 공유합니다.', CURDATE(), '음악', 'PLAYLIST', 5, 35),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440010', '-', '')), '쇼핑몰 추천 가이드', '합리적인 쇼핑을 위한 온라인 쇼핑몰 비교. 가격과 품질을 분석합니다.', CURDATE(), '쇼핑', 'COMPARISON', 3, 22),
(UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440011', '-', '')), '스터디 카페 가이드', '집중력 향상을 위한 스터디 카페 추천. 조용하고 쾌적한 공간을 소개합니다.', CURDATE(), '학습', 'REVIEW', 4, 28);

-- =====================================================
-- 10. GuideAndUser Table (Guide Likes)
-- =====================================================
INSERT INTO guide_and_user (user_id, guide_id) VALUES
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440001', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440001', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440002', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440003', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440003', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440004', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440005', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440006', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440007', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440008', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440009', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440010', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440011', '-', ''))),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440012', '-', '')));

-- =====================================================
-- 11. Tail Table
-- =====================================================
INSERT INTO tail (id, vote_id, question) VALUES
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), '프로그래밍을 배우게 된 계기는 무엇인가요?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440002', '-', '')), '어떤 음식 스타일을 선호하시나요?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440003', '-', '')), '여행 시 가장 중요하게 생각하는 것은?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440004', '-', '')), '영화를 볼 때 무엇을 중시하나요?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440005', '-', '')), '책을 읽는 주된 목적은?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440006', '-', '')), '운동을 통해 얻고 싶은 것은?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440007', '-', '')), '카페에서 가장 중요한 요소는?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440008', '-', '')), '게임을 할 때 선호하는 장르는?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440009', '-', '')), '음악 감상 시 중요한 것은?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440010', '-', '')), '쇼핑 시 가장 중요하게 생각하는 것은?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440011', '-', '')), '스터디 카페에서 가장 중요한 것은?'),
(UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440012', '-', '')), '반려동물을 키울 때 고려사항은?');

-- =====================================================
-- 12. TailResponse Table
-- =====================================================
INSERT INTO tail_response (id, user_id, tail_id, answer) VALUES
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440001', '-', '')), '취업을 위해 시작했습니다'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440002', '-', '')), '매운 음식을 좋아합니다'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440003', '-', '')), '경치가 아름다운 곳'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440004', '-', '')), '스토리 전개가 중요합니다'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440005', '-', '')), '지식 습득이 목적입니다'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440006', '-', '')), '건강한 몸을 만들고 싶습니다'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440007', '-', '')), '분위기가 가장 중요합니다'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440008', '-', '')), 'RPG 게임을 좋아합니다'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440009', '-', '')), '가사가 좋은 노래'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440010', '-', '')), '가격 대비 품질'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440011', '-', '')), '조용한 환경'),
(UNHEX(REPLACE('bb0e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('aa0e8400-e29b-41d4-a716-446655440012', '-', '')), '털 빠짐 정도');

-- =====================================================
-- 13. Board Table
-- =====================================================
INSERT INTO boards (id, title, content, user_id, view_count, created_at, updated_at) VALUES
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440001', '-', '')), '개발 관련 질문', '스프링 부트 관련 질문입니다', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), 45, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440002', '-', '')), '프로젝트 후기', '졸업 프로젝트 개발 후기 공유', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), 78, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440003', '-', '')), '알고리즘 스터디 모집', '알고리즘 스터디원 구합니다', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), 120, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440004', '-', '')), 'React vs Vue', '프론트엔드 프레임워크 비교', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), 95, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440005', '-', '')), '데이터베이스 설계 팁', 'ERD 작성 시 유의사항', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), 67, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440006', '-', '')), 'Git 사용 꿀팁', 'Git 명령어 모음집', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), 150, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440007', '-', '')), '코딩 테스트 준비', '코딩 테스트 공부 방법 공유', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), 200, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440008', '-', '')), 'Docker 입문', 'Docker 컨테이너 기초', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), 88, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440009', '-', '')), 'AWS 비용 절감', 'AWS 요금 최적화 방법', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), 112, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440010', '-', '')), '개발자 취업 후기', '신입 개발자 취업 경험 공유', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), 175, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440011', '-', '')), 'Clean Code 독후감', '클린 코드 읽고 느낀 점', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), 92, NOW(), NOW()),
(UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440012', '-', '')), 'TDD 실천기', 'TDD 개발 방법론 적용 후기', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), 105, NOW(), NOW());

-- =====================================================
-- 14. BoardLike Table
-- =====================================================
INSERT INTO board_likes (id, user_id, board_id, created_at) VALUES
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440001', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440002', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440003', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440004', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440005', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440006', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440007', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440008', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440009', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440010', '-', '')), NOW()),
(UNHEX(REPLACE('dd0e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440011', '-', '')), NOW());

-- =====================================================
-- 15. Comment Table
-- =====================================================
INSERT INTO comments (id, content, user_id, board_id, parent_id, created_at, updated_at) VALUES
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440001', '-', '')), '좋은 정보 감사합니다!', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440001', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440002', '-', '')), '저도 같은 문제를 겪었어요', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440001', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440003', '-', '')), '대댓글 테스트', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440001', '-', '')), NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440004', '-', '')), '프로젝트 정말 멋지네요', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440002', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440005', '-', '')), '스터디 참여하고 싶습니다', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440003', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440006', '-', '')), 'React가 더 좋은 것 같아요', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440004', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440007', '-', '')), '유용한 팁 감사합니다', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440005', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440008', '-', '')), 'Git 명령어 정리 잘 봤어요', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440006', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440009', '-', '')), '코테 준비 화이팅!', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440007', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440010', '-', '')), 'Docker 공부 시작해야겠네요', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440008', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440011', '-', '')), 'AWS 비용 절감 팁 좋네요', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440009', '-', '')), NULL, NOW(), NOW()),
(UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440012', '-', '')), '취업 축하드립니다!', UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440010', '-', '')), NULL, NOW(), NOW());

-- =====================================================
-- 16. CommentLike Table
-- =====================================================
INSERT INTO comment_likes (id, user_id, comment_id, created_at) VALUES
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440001', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440002', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440003', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440004', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440005', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440006', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440007', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440008', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440009', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440010', '-', '')), NOW()),
(UNHEX(REPLACE('ff0e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440011', '-', '')), NOW());

-- =====================================================
-- 17. Bookmark Table
-- =====================================================
INSERT INTO bookmark (id, user_id, guide_id, created_at) VALUES
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440001', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440002', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440003', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440004', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440005', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440006', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440007', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440008', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440009', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440010', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440011', '-', '')), CURDATE()),
(UNHEX(REPLACE('100e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440012', '-', '')), CURDATE());

-- =====================================================
-- 18. Report Table
-- =====================================================
INSERT INTO report (id, user_id, target_id, reason, created_at) VALUES
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440001', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440001', '-', '')), '스팸 게시물', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440002', '-', '')), '부적절한 내용', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440001', '-', '')), '욕설 포함', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440002', '-', '')), '허위 정보', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440001', '-', '')), '저작권 침해', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440002', '-', '')), '중복 게시물', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440003', '-', '')), '광고성 게시물', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('cc0e8400-e29b-41d4-a716-446655440004', '-', '')), '개인정보 노출', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440003', '-', '')), '혐오 발언', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440004', '-', '')), '도배 행위', CURDATE()),
(UNHEX(REPLACE('110e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440003', '-', '')), '성적 콘텐츠', CURDATE());

-- =====================================================
-- 19. RevoteRequest Table
-- =====================================================
INSERT INTO revote_request (user_id, guide_id, reason, detail_reason, status, created_at, updated_at) VALUES
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440001', '-', '')), '정보 부족', '더 자세한 설명이 필요합니다', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440002', '-', '')), '오래된 정보', '최신 정보로 업데이트가 필요합니다', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440003', '-', '')), '부정확한 정보', '일부 내용이 사실과 다릅니다', 'APPROVED', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440004', '-', '')), '추가 옵션 필요', '더 많은 선택지가 필요합니다', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440005', '-', '')), '불완전한 가이드', '내용이 불완전합니다', 'REJECTED', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440006', '-', '')), '카테고리 변경', '카테고리가 적절하지 않습니다', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440007', '-', '')), '중복 내용', '다른 가이드와 중복됩니다', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440008', '-', '')), '품질 개선', '가이드 품질 향상이 필요합니다', 'APPROVED', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440009', '-', '')), '예시 추가', '실제 사례가 더 필요합니다', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440010', '-', '')), '링크 추가', '참고 자료 링크가 필요합니다', 'PENDING', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440011', '-', '')), '이미지 추가', '시각 자료가 필요합니다', 'APPROVED', NOW(), NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440012', '-', '')), '구체적 사례 필요', '더 구체적인 예시가 필요합니다', 'PENDING', NOW(), NOW());

-- =====================================================
-- 20. Notification Table
-- =====================================================
INSERT INTO notification (user_id, type, title, content, reference_id, `read`, created_at) VALUES
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), 'VOTE_ENDED', '투표 종료', '참여하신 투표가 종료되었습니다', UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440006', '-', '')), false, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440003', '-', '')), 'GUIDE_CREATED', '가이드 생성', '새로운 가이드가 생성되었습니다', UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440001', '-', '')), false, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440004', '-', '')), 'QUESTION_REPLY', '댓글 알림', '회원님의 게시물에 댓글이 달렸습니다', UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440001', '-', '')), true, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440005', '-', '')), 'VOTE_ENDED', '투표 종료', '투표가 종료되고 가이드가 생성되었습니다', UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440007', '-', '')), false, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440006', '-', '')), 'GUIDE_CREATED', '가이드 생성', '관심있는 주제의 가이드가 생성되었습니다', UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440002', '-', '')), true, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440007', '-', '')), 'QUESTION_REPLY', '댓글 알림', '회원님의 댓글에 답글이 달렸습니다', UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440003', '-', '')), false, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440008', '-', '')), 'VOTE_ENDED', '투표 종료', '생성하신 투표가 종료되었습니다', UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440013', '-', '')), true, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440009', '-', '')), 'GUIDE_CREATED', '가이드 생성', '투표 결과로 가이드가 생성되었습니다', UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440003', '-', '')), false, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440010', '-', '')), 'QUESTION_REPLY', '댓글 알림', '새로운 댓글이 작성되었습니다', UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440004', '-', '')), false, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440011', '-', '')), 'VOTE_ENDED', '투표 종료', '투표 참여 감사합니다', UNHEX(REPLACE('660e8400-e29b-41d4-a716-446655440001', '-', '')), true, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440012', '-', '')), 'GUIDE_CREATED', '가이드 생성', '북마크한 투표의 가이드가 생성되었습니다', UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440004', '-', '')), false, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440013', '-', '')), 'QUESTION_REPLY', '댓글 알림', '게시물에 새로운 댓글이 달렸습니다', UNHEX(REPLACE('ee0e8400-e29b-41d4-a716-446655440005', '-', '')), true, NOW()),
(UNHEX(REPLACE('550e8400-e29b-41d4-a716-446655440002', '-', '')), 'GUIDE_CREATED', '가이드 생성', '즐겨찾기한 투표의 가이드가 완성되었습니다', UNHEX(REPLACE('990e8400-e29b-41d4-a716-446655440005', '-', '')), false, NOW());

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Verification Queries
-- =====================================================
SELECT '✅ 데이터 삽입 완료!' AS status;

-- Count records in each table
SELECT 'user' AS table_name, COUNT(*) AS record_count FROM user
UNION ALL SELECT 'refresh_token', COUNT(*) FROM refresh_token
UNION ALL SELECT 'AccessTokenCode', COUNT(*) FROM AccessTokenCode
UNION ALL SELECT 'RefreshTokenCode', COUNT(*) FROM RefreshTokenCode
UNION ALL SELECT 'aiusage_quota', COUNT(*) FROM aiusage_quota
UNION ALL SELECT 'vote', COUNT(*) FROM vote
UNION ALL SELECT 'option', COUNT(*) FROM `option`
UNION ALL SELECT 'vote_response', COUNT(*) FROM vote_response
UNION ALL SELECT 'guide', COUNT(*) FROM guide
UNION ALL SELECT 'guide_and_user', COUNT(*) FROM guide_and_user
UNION ALL SELECT 'tail', COUNT(*) FROM tail
UNION ALL SELECT 'tail_response', COUNT(*) FROM tail_response
UNION ALL SELECT 'boards', COUNT(*) FROM boards
UNION ALL SELECT 'board_likes', COUNT(*) FROM board_likes
UNION ALL SELECT 'comments', COUNT(*) FROM comments
UNION ALL SELECT 'comment_likes', COUNT(*) FROM comment_likes
UNION ALL SELECT 'bookmark', COUNT(*) FROM bookmark
UNION ALL SELECT 'report', COUNT(*) FROM report
UNION ALL SELECT 'revote_request', COUNT(*) FROM revote_request
UNION ALL SELECT 'notification', COUNT(*) FROM notification
ORDER BY table_name;

-- =====================================================
-- End of Script
-- =====================================================
