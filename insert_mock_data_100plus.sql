-- =====================================================
-- Mock Data Insertion Script for UPIK Database (100+ Records)
-- =====================================================
-- This script inserts 100+ records into each table
-- Topics: Coding & School Life
-- Run this after database schema is created
-- =====================================================

USE upik;

-- Disable foreign key checks for easier insertion
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. User Table (100+ users)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_users$$
CREATE PROCEDURE insert_users()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE username_val VARCHAR(50);
  DECLARE name_val VARCHAR(50);
  DECLARE email_val VARCHAR(100);

  -- Admin user
  INSERT INTO user (id, role, username, name, email, created_at, recent_date, dollar, won, streak_count) VALUES
  (UNHEX(REPLACE(UUID(), '-', '')), 'ADMIN', 'admin', '관리자', 'admin@upik.com', NOW(), NOW(), 1000.0, 500000, 100);

  WHILE i <= 150 DO
    SET user_uuid = UNHEX(REPLACE(UUID(), '-', ''));
    SET username_val = CONCAT('student', LPAD(i, 3, '0'));
    SET name_val = CONCAT('학생', i);
    SET email_val = CONCAT('student', i, '@school.edu');

    INSERT INTO user (id, role, username, name, email, created_at, recent_date, dollar, won, streak_count)
    VALUES (
      user_uuid,
      'USER',
      username_val,
      name_val,
      email_val,
      NOW() - INTERVAL FLOOR(RAND() * 365) DAY,
      NOW() - INTERVAL FLOOR(RAND() * 30) DAY,
      ROUND(RAND() * 200, 2),
      FLOOR(RAND() * 100000),
      FLOOR(RAND() * 50)
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_users();
DROP PROCEDURE insert_users;

-- =====================================================
-- 2. RefreshToken Table (100+ tokens)
-- =====================================================
INSERT INTO refresh_token (user_id, token, role, expiry_date)
SELECT
  id,
  CONCAT('refresh_token_', username),
  role,
  DATE_ADD(NOW(), INTERVAL 30 DAY)
FROM user
LIMIT 120;

-- =====================================================
-- 3. AccessTokenCode Table (100+ codes)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_access_token_codes$$
CREATE PROCEDURE insert_access_token_codes()
BEGIN
  DECLARE i INT DEFAULT 1;
  WHILE i <= 120 DO
    INSERT INTO AccessTokenCode (code, accessToken)
    VALUES (
      CONCAT('auth_code_', LPAD(i, 4, '0')),
      CONCAT('access_token_', LPAD(i, 4, '0'))
    );
    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_access_token_codes();
DROP PROCEDURE insert_access_token_codes;

-- =====================================================
-- 4. RefreshTokenCode Table (100+ codes)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_refresh_token_codes$$
CREATE PROCEDURE insert_refresh_token_codes()
BEGIN
  DECLARE i INT DEFAULT 1;
  WHILE i <= 120 DO
    INSERT INTO RefreshTokenCode (code, refreshToken)
    VALUES (
      CONCAT('refresh_code_', LPAD(i, 4, '0')),
      CONCAT('refresh_token_code_', LPAD(i, 4, '0'))
    );
    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_refresh_token_codes();
DROP PROCEDURE insert_refresh_token_codes;

-- =====================================================
-- 5. AIUsageQuota Table (100+ quotas)
-- =====================================================
INSERT INTO aiusage_quota (user_id, usage_count, last_reset_date, updated_at)
SELECT
  id,
  FLOOR(RAND() * 10),
  CURDATE() - INTERVAL FLOOR(RAND() * 7) DAY,
  NOW()
FROM user
LIMIT 120;

-- =====================================================
-- 6. Vote Table (150+ votes with coding/school topics)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_votes$$
CREATE PROCEDURE insert_votes()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE vote_uuid BINARY(16);
  DECLARE creator_uuid BINARY(16);
  DECLARE question_val TEXT;
  DECLARE category_val VARCHAR(50);
  DECLARE status_val VARCHAR(10);

  -- Coding topics array
  DECLARE coding_topics TEXT DEFAULT 'Java vs Python vs JavaScript|가장 좋은 IDE는?|프론트엔드 프레임워크 선택|백엔드 프레임워크 추천|데이터베이스 선택|클라우드 서비스 추천|버전 관리 도구|코드 에디터 추천|알고리즘 공부 방법|자료구조 학습 순서|개발 공부 로드맵|코딩 테스트 준비|프로젝트 주제 선정|오픈소스 기여 방법|기술 블로그 추천|개발자 커뮤니티|개발 유튜브 채널|프로그래밍 책 추천|코딩 부트캠프 선택|온라인 강의 플랫폼|협업 툴 추천|API 문서화 도구|테스트 프레임워크|CI/CD 도구|컨테이너 기술|마이크로서비스 vs 모놀리식|REST vs GraphQL|SQL vs NoSQL|개발 방법론|애자일 vs 워터폴';

  -- School topics array
  DECLARE school_topics TEXT DEFAULT '중간고사 공부 방법|기말고사 준비 전략|팀플 역할 분담|과제 마감 관리|학식 메뉴 추천|도서관 좌석 선호도|동아리 선택|학생회 활동|교양 수업 추천|전공 수업 난이도|교수님 강의 스타일|실습실 이용 시간|스터디 그룹 구성|학점 관리 방법|복수전공 선택|휴학 시기|인턴십 준비|취업 vs 대학원|학교 행사 참여|MT 장소 추천|축제 부스 아이디어|졸업 요건 확인|장학금 신청 방법|학생증 혜택|통학 vs 자취|학교 주변 맛집|카페 공부 vs 도서관|방학 계획|계절학기 수강|학교 시설 개선';

  WHILE i <= 180 DO
    SET vote_uuid = UNHEX(REPLACE(UUID(), '-', ''));

    -- Get random creator from users
    SELECT id INTO creator_uuid FROM user ORDER BY RAND() LIMIT 1;

    -- Alternate between coding and school topics
    IF i % 2 = 1 THEN
      SET question_val = SUBSTRING_INDEX(SUBSTRING_INDEX(coding_topics, '|', FLOOR(1 + RAND() * 30)), '|', -1);
      SET category_val = ELT(FLOOR(1 + RAND() * 5), '프로그래밍', '개발도구', '프레임워크', '알고리즘', '웹개발');
    ELSE
      SET question_val = SUBSTRING_INDEX(SUBSTRING_INDEX(school_topics, '|', FLOOR(1 + RAND() * 30)), '|', -1);
      SET category_val = ELT(FLOOR(1 + RAND() * 5), '수업', '시험', '과제', '동아리', '학교생활');
    END IF;

    -- 70% OPEN, 30% CLOSED
    IF RAND() < 0.7 THEN
      SET status_val = 'OPEN';
    ELSE
      SET status_val = 'CLOSED';
    END IF;

    INSERT INTO vote (id, creator_id, question, category, status, finished_at, closure_type, participant_threshold)
    VALUES (
      vote_uuid,
      creator_uuid,
      question_val,
      category_val,
      status_val,
      IF(status_val = 'CLOSED', CURDATE(), DATE_ADD(CURDATE(), INTERVAL FLOOR(1 + RAND() * 10) DAY)),
      ELT(FLOOR(1 + RAND() * 3), 'DEFAULT', 'CUSTOM_DAYS', 'PARTICIPANT_COUNT'),
      IF(RAND() < 0.3, FLOOR(20 + RAND() * 80), NULL)
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_votes();
DROP PROCEDURE insert_votes;

-- =====================================================
-- 7. Option Table (500+ options, 3-4 per vote)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_options$$
CREATE PROCEDURE insert_options()
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE vote_uuid BINARY(16);
  DECLARE option_count INT;
  DECLARE i INT;
  DECLARE cur CURSOR FOR SELECT id FROM vote;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO vote_uuid;
    IF done THEN
      LEAVE read_loop;
    END IF;

    -- 3-4 options per vote
    SET option_count = FLOOR(3 + RAND() * 2);
    SET i = 1;

    WHILE i <= option_count DO
      INSERT INTO `option` (id, vote_id, content)
      VALUES (
        UNHEX(REPLACE(UUID(), '-', '')),
        vote_uuid,
        CONCAT('선택지 ', i)
      );
      SET i = i + 1;
    END WHILE;
  END LOOP;

  CLOSE cur;
END$$
DELIMITER ;

CALL insert_options();
DROP PROCEDURE insert_options;

-- =====================================================
-- 8. VoteResponse Table (500+ responses)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_vote_responses$$
CREATE PROCEDURE insert_vote_responses()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE vote_uuid BINARY(16);
  DECLARE option_uuid BINARY(16);

  WHILE i <= 600 DO
    -- Random user
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;

    -- Random vote
    SELECT id INTO vote_uuid FROM vote ORDER BY RAND() LIMIT 1;

    -- Random option from that vote
    SELECT id INTO option_uuid FROM `option` WHERE vote_id = vote_uuid ORDER BY RAND() LIMIT 1;

    IF option_uuid IS NOT NULL THEN
      INSERT IGNORE INTO vote_response (id, user_id, vote_id, option_id, created_at)
      VALUES (
        UNHEX(REPLACE(UUID(), '-', '')),
        user_uuid,
        vote_uuid,
        option_uuid,
        CURDATE() - INTERVAL FLOOR(RAND() * 30) DAY
      );
    END IF;

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_vote_responses();
DROP PROCEDURE insert_vote_responses;

-- =====================================================
-- 9. Guide Table (100+ guides with coding/school topics)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_guides$$
CREATE PROCEDURE insert_guides()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE guide_uuid BINARY(16);
  DECLARE vote_uuid BINARY(16);
  DECLARE title_val VARCHAR(255);
  DECLARE content_val TEXT;
  DECLARE category_val VARCHAR(50);
  DECLARE done INT DEFAULT FALSE;
  DECLARE cur CURSOR FOR SELECT id, category FROM vote WHERE status = 'CLOSED' LIMIT 120;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO vote_uuid, category_val;
    IF done THEN
      LEAVE read_loop;
    END IF;

    SET guide_uuid = UNHEX(REPLACE(UUID(), '-', ''));
    SET title_val = CONCAT(category_val, ' 가이드 ', i);
    SET content_val = CONCAT('이 가이드는 ', category_val, '에 대한 종합적인 정보를 제공합니다. ',
      '투표 결과를 바탕으로 작성되었으며, 다양한 의견과 경험을 반영하고 있습니다. ',
      '초보자부터 고급 사용자까지 모두에게 유용한 정보를 담고 있습니다.');

    INSERT IGNORE INTO guide (id, vote_id, title, content, created_at, category, guide_type, revote_count, `like`)
    VALUES (
      guide_uuid,
      vote_uuid,
      title_val,
      content_val,
      CURDATE() - INTERVAL FLOOR(RAND() * 60) DAY,
      category_val,
      ELT(FLOOR(1 + RAND() * 8), 'COMPREHENSIVE', 'REVIEW', 'BUYING_GUIDE', 'BEGINNER', 'TIPS', 'COMPARISON', 'HOW_TO', 'SPECIALIZED'),
      FLOOR(RAND() * 20),
      FLOOR(RAND() * 100)
    );

    SET i = i + 1;
  END LOOP;

  CLOSE cur;
END$$
DELIMITER ;

CALL insert_guides();
DROP PROCEDURE insert_guides;

-- =====================================================
-- 10. GuideAndUser Table (300+ likes)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_guide_likes$$
CREATE PROCEDURE insert_guide_likes()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE guide_uuid BINARY(16);

  WHILE i <= 400 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SELECT id INTO guide_uuid FROM guide ORDER BY RAND() LIMIT 1;

    INSERT IGNORE INTO guide_and_user (user_id, guide_id)
    VALUES (user_uuid, guide_uuid);

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_guide_likes();
DROP PROCEDURE insert_guide_likes;

-- =====================================================
-- 11. Tail Table (150+ additional questions)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_tails$$
CREATE PROCEDURE insert_tails()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE tail_uuid BINARY(16);
  DECLARE vote_uuid BINARY(16);
  DECLARE question_val TEXT;
  DECLARE done INT DEFAULT FALSE;
  DECLARE cur CURSOR FOR SELECT id FROM vote LIMIT 150;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO vote_uuid;
    IF done THEN
      LEAVE read_loop;
    END IF;

    SET tail_uuid = UNHEX(REPLACE(UUID(), '-', ''));

    -- Coding or school related follow-up questions
    IF i % 2 = 1 THEN
      SET question_val = ELT(FLOOR(1 + RAND() * 10),
        '프로그래밍 경력은 얼마나 되셨나요?',
        '어떤 프로젝트 경험이 있으신가요?',
        '선호하는 개발 환경은 무엇인가요?',
        '코딩할 때 가장 중요하게 생각하는 것은?',
        '어떤 방식으로 학습하시나요?',
        '오픈소스 기여 경험이 있으신가요?',
        '팀 프로젝트 경험은 어떠신가요?',
        '코드 리뷰를 받아본 경험이 있나요?',
        '버전 관리는 어떻게 하시나요?',
        '기술 스택 선택 기준은 무엇인가요?'
      );
    ELSE
      SET question_val = ELT(FLOOR(1 + RAND() * 10),
        '학년은 어떻게 되시나요?',
        '전공은 무엇인가요?',
        '학점은 어느 정도 관리하시나요?',
        '하루 평균 공부 시간은?',
        '동아리 활동은 하시나요?',
        '교내 활동에 적극적으로 참여하시나요?',
        '스터디 그룹에 참여하시나요?',
        '학교 시설을 자주 이용하시나요?',
        '진로는 정하셨나요?',
        '졸업 후 계획은 무엇인가요?'
      );
    END IF;

    INSERT INTO tail (id, vote_id, question)
    VALUES (tail_uuid, vote_uuid, question_val);

    SET i = i + 1;
  END LOOP;

  CLOSE cur;
END$$
DELIMITER ;

CALL insert_tails();
DROP PROCEDURE insert_tails;

-- =====================================================
-- 12. TailResponse Table (300+ responses)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_tail_responses$$
CREATE PROCEDURE insert_tail_responses()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE response_uuid BINARY(16);
  DECLARE user_uuid BINARY(16);
  DECLARE tail_uuid BINARY(16);
  DECLARE answer_val TEXT;

  WHILE i <= 400 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SELECT id INTO tail_uuid FROM tail ORDER BY RAND() LIMIT 1;

    SET response_uuid = UNHEX(REPLACE(UUID(), '-', ''));
    SET answer_val = CONCAT('답변 내용 ', i, ': 개인적인 경험과 생각을 바탕으로 작성했습니다.');

    INSERT IGNORE INTO tail_response (id, user_id, tail_id, answer)
    VALUES (response_uuid, user_uuid, tail_uuid, answer_val);

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_tail_responses();
DROP PROCEDURE insert_tail_responses;

-- =====================================================
-- 13. Board Table (150+ posts with coding/school topics)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_boards$$
CREATE PROCEDURE insert_boards()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE board_uuid BINARY(16);
  DECLARE user_uuid BINARY(16);
  DECLARE title_val VARCHAR(255);
  DECLARE content_val TEXT;

  WHILE i <= 200 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SET board_uuid = UNHEX(REPLACE(UUID(), '-', ''));

    -- Alternate between coding and school topics
    IF i % 2 = 1 THEN
      SET title_val = ELT(FLOOR(1 + RAND() * 15),
        'Spring Boot 관련 질문',
        'React 컴포넌트 설계 고민',
        'MySQL vs PostgreSQL 선택',
        'Docker 배포 에러 해결',
        'Git 충돌 해결 방법',
        'REST API 설계 패턴',
        'JWT 인증 구현 방법',
        'JPA N+1 문제 해결',
        '알고리즘 시간복잡도 분석',
        '자료구조 선택 기준',
        'Clean Code 적용 사례',
        '테스트 코드 작성 팁',
        'CI/CD 파이프라인 구축',
        '코드 리팩토링 경험',
        '디자인 패턴 적용 사례'
      );
      SET content_val = CONCAT('안녕하세요. ', title_val, '에 대해 궁금한 점이 있어 질문 드립니다. ',
        '현재 프로젝트를 진행하면서 어려움을 겪고 있는데, 경험 있으신 분들의 조언을 구합니다. ',
        '관련된 자료나 레퍼런스가 있다면 공유해주시면 감사하겠습니다.');
    ELSE
      SET title_val = ELT(FLOOR(1 + RAND() * 15),
        '중간고사 공부 방법 공유',
        '팀 프로젝트 역할 분담 꿀팁',
        '교양 수업 추천해주세요',
        '학식 맛집 메뉴 공유',
        '도서관 좌석 예약 팁',
        '동아리 활동 후기',
        '스터디 그룹 모집합니다',
        '전공 수업 난이도 정보',
        '장학금 신청 경험담',
        '인턴 준비 과정 공유',
        '취업 vs 대학원 고민',
        '복수전공 선택 조언',
        '학점 관리 노하우',
        '과제 마감 관리 방법',
        '학교 행사 참여 후기'
      );
      SET content_val = CONCAT(title_val, '에 대한 내용입니다. ',
        '학교 생활을 하면서 느낀 점들을 공유하고자 합니다. ',
        '같은 고민을 하고 계신 분들께 도움이 되었으면 좋겠습니다. ',
        '여러분의 의견과 경험도 함께 나눠주시면 감사하겠습니다.');
    END IF;

    INSERT INTO boards (id, title, content, user_id, view_count, created_at, updated_at)
    VALUES (
      board_uuid,
      title_val,
      content_val,
      user_uuid,
      FLOOR(RAND() * 500),
      NOW() - INTERVAL FLOOR(RAND() * 180) DAY,
      NOW() - INTERVAL FLOOR(RAND() * 90) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_boards();
DROP PROCEDURE insert_boards;

-- =====================================================
-- 14. BoardLike Table (300+ likes)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_board_likes$$
CREATE PROCEDURE insert_board_likes()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE board_uuid BINARY(16);

  WHILE i <= 400 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SELECT id INTO board_uuid FROM boards ORDER BY RAND() LIMIT 1;

    INSERT IGNORE INTO board_likes (id, user_id, board_id, created_at)
    VALUES (
      UNHEX(REPLACE(UUID(), '-', '')),
      user_uuid,
      board_uuid,
      NOW() - INTERVAL FLOOR(RAND() * 90) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_board_likes();
DROP PROCEDURE insert_board_likes;

-- =====================================================
-- 15. Comment Table (500+ comments)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_comments$$
CREATE PROCEDURE insert_comments()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE comment_uuid BINARY(16);
  DECLARE user_uuid BINARY(16);
  DECLARE board_uuid BINARY(16);
  DECLARE content_val TEXT;

  WHILE i <= 600 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SELECT id INTO board_uuid FROM boards ORDER BY RAND() LIMIT 1;
    SET comment_uuid = UNHEX(REPLACE(UUID(), '-', ''));

    SET content_val = ELT(FLOOR(1 + RAND() * 15),
      '좋은 정보 감사합니다!',
      '저도 같은 문제를 겪었는데 도움이 되었어요.',
      '추가로 이런 방법도 있습니다.',
      '궁금한 점이 있는데 질문해도 될까요?',
      '실제로 적용해보니 잘 되네요!',
      '이 부분은 다시 생각해볼 필요가 있을 것 같아요.',
      '경험상 이렇게 하는 게 더 효율적입니다.',
      '관련 자료 링크 공유드립니다.',
      '저는 다른 방식으로 해결했어요.',
      '비슷한 케이스를 본 적이 있습니다.',
      '상세한 설명 감사합니다.',
      '이해하기 쉽게 정리해주셨네요.',
      '참고할 만한 정보입니다.',
      '실용적인 팁이네요!',
      '다음에 꼭 시도해보겠습니다.'
    );

    INSERT INTO comments (id, content, user_id, board_id, parent_id, created_at, updated_at)
    VALUES (
      comment_uuid,
      content_val,
      user_uuid,
      board_uuid,
      NULL,
      NOW() - INTERVAL FLOOR(RAND() * 60) DAY,
      NOW() - INTERVAL FLOOR(RAND() * 30) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_comments();
DROP PROCEDURE insert_comments;

-- =====================================================
-- 16. CommentLike Table (400+ likes)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_comment_likes$$
CREATE PROCEDURE insert_comment_likes()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE comment_uuid BINARY(16);

  WHILE i <= 500 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SELECT id INTO comment_uuid FROM comments ORDER BY RAND() LIMIT 1;

    INSERT IGNORE INTO comment_likes (id, user_id, comment_id, created_at)
    VALUES (
      UNHEX(REPLACE(UUID(), '-', '')),
      user_uuid,
      comment_uuid,
      NOW() - INTERVAL FLOOR(RAND() * 60) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_comment_likes();
DROP PROCEDURE insert_comment_likes;

-- =====================================================
-- 17. Bookmark Table (300+ bookmarks)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_bookmarks$$
CREATE PROCEDURE insert_bookmarks()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE guide_uuid BINARY(16);

  WHILE i <= 400 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SELECT id INTO guide_uuid FROM guide ORDER BY RAND() LIMIT 1;

    INSERT IGNORE INTO bookmark (id, user_id, guide_id, created_at)
    VALUES (
      UNHEX(REPLACE(UUID(), '-', '')),
      user_uuid,
      guide_uuid,
      CURDATE() - INTERVAL FLOOR(RAND() * 90) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_bookmarks();
DROP PROCEDURE insert_bookmarks;

-- =====================================================
-- 18. Report Table (150+ reports)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_reports$$
CREATE PROCEDURE insert_reports()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE report_uuid BINARY(16);
  DECLARE user_uuid BINARY(16);
  DECLARE target_uuid BINARY(16);
  DECLARE reason_val VARCHAR(255);

  WHILE i <= 200 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;

    -- Report either board or comment
    IF RAND() < 0.5 THEN
      SELECT id INTO target_uuid FROM boards ORDER BY RAND() LIMIT 1;
    ELSE
      SELECT id INTO target_uuid FROM comments ORDER BY RAND() LIMIT 1;
    END IF;

    SET report_uuid = UNHEX(REPLACE(UUID(), '-', ''));
    SET reason_val = ELT(FLOOR(1 + RAND() * 10),
      '스팸 게시물',
      '부적절한 내용',
      '욕설 포함',
      '허위 정보',
      '중복 게시물',
      '광고성 게시물',
      '개인정보 노출',
      '혐오 발언',
      '도배 행위',
      '저작권 침해'
    );

    INSERT INTO report (id, user_id, target_id, reason, created_at)
    VALUES (
      report_uuid,
      user_uuid,
      target_uuid,
      reason_val,
      CURDATE() - INTERVAL FLOOR(RAND() * 60) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_reports();
DROP PROCEDURE insert_reports;

-- =====================================================
-- 19. RevoteRequest Table (200+ requests)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_revote_requests$$
CREATE PROCEDURE insert_revote_requests()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE guide_uuid BINARY(16);
  DECLARE reason_val VARCHAR(255);
  DECLARE detail_val TEXT;
  DECLARE status_val VARCHAR(20);

  WHILE i <= 250 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;
    SELECT id INTO guide_uuid FROM guide ORDER BY RAND() LIMIT 1;

    SET reason_val = ELT(FLOOR(1 + RAND() * 10),
      '정보 부족',
      '오래된 정보',
      '부정확한 정보',
      '추가 옵션 필요',
      '불완전한 가이드',
      '카테고리 변경 필요',
      '중복 내용',
      '품질 개선 필요',
      '예시 추가 요청',
      '링크 추가 요청'
    );

    SET detail_val = CONCAT(reason_val, '로 인해 재투표가 필요합니다. 더 나은 가이드를 위해 다시 의견을 수렴하면 좋겠습니다.');

    SET status_val = ELT(FLOOR(1 + RAND() * 3), 'PENDING', 'APPROVED', 'REJECTED');

    INSERT IGNORE INTO revote_request (user_id, guide_id, reason, detail_reason, status, created_at, updated_at)
    VALUES (
      user_uuid,
      guide_uuid,
      reason_val,
      detail_val,
      status_val,
      NOW() - INTERVAL FLOOR(RAND() * 90) DAY,
      NOW() - INTERVAL FLOOR(RAND() * 30) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_revote_requests();
DROP PROCEDURE insert_revote_requests;

-- =====================================================
-- 20. Notification Table (300+ notifications)
-- =====================================================
DELIMITER $$
DROP PROCEDURE IF EXISTS insert_notifications$$
CREATE PROCEDURE insert_notifications()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE user_uuid BINARY(16);
  DECLARE ref_uuid BINARY(16);
  DECLARE type_val VARCHAR(50);
  DECLARE title_val VARCHAR(255);
  DECLARE content_val TEXT;
  DECLARE read_val BOOLEAN;

  WHILE i <= 400 DO
    SELECT id INTO user_uuid FROM user WHERE role = 'USER' ORDER BY RAND() LIMIT 1;

    -- Random notification type
    SET type_val = ELT(FLOOR(1 + RAND() * 3), 'VOTE_ENDED', 'GUIDE_CREATED', 'QUESTION_REPLY');

    IF type_val = 'VOTE_ENDED' THEN
      SELECT id INTO ref_uuid FROM vote ORDER BY RAND() LIMIT 1;
      SET title_val = '투표 종료';
      SET content_val = '참여하신 투표가 종료되었습니다';
    ELSEIF type_val = 'GUIDE_CREATED' THEN
      SELECT id INTO ref_uuid FROM guide ORDER BY RAND() LIMIT 1;
      SET title_val = '가이드 생성';
      SET content_val = '새로운 가이드가 생성되었습니다';
    ELSE
      SELECT id INTO ref_uuid FROM comments ORDER BY RAND() LIMIT 1;
      SET title_val = '댓글 알림';
      SET content_val = '회원님의 게시물에 댓글이 달렸습니다';
    END IF;

    SET read_val = RAND() < 0.6; -- 60% read

    INSERT INTO notification (user_id, type, title, content, reference_id, `read`, created_at)
    VALUES (
      user_uuid,
      type_val,
      title_val,
      content_val,
      ref_uuid,
      read_val,
      NOW() - INTERVAL FLOOR(RAND() * 60) DAY
    );

    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL insert_notifications();
DROP PROCEDURE insert_notifications;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Verification Queries
-- =====================================================
SELECT '✅ 대량 데이터 삽입 완료!' AS status;
SELECT '📊 각 테이블별 레코드 수를 확인하세요.' AS info;

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
-- Sample Data Queries (코딩/학교 주제 확인)
-- =====================================================
SELECT '📝 투표 샘플 (코딩/학교 주제):' AS info;
SELECT question, category, status FROM vote LIMIT 10;

SELECT '📚 가이드 샘플:' AS info;
SELECT title, category, guide_type FROM guide LIMIT 10;

SELECT '💬 게시판 샘플:' AS info;
SELECT title FROM boards LIMIT 10;

-- =====================================================
-- End of Script
-- =====================================================
