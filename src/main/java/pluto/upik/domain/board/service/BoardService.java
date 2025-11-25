package pluto.upik.domain.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.board.data.DTO.*;
import pluto.upik.domain.board.data.model.Board;
import pluto.upik.domain.board.data.model.Comment;
import pluto.upik.domain.board.repository.BoardRepository;
import pluto.upik.domain.board.repository.CommentRepository;
import pluto.upik.domain.bookmark.data.model.BoardBookmark;
import pluto.upik.domain.bookmark.repository.BoardBookmarkRepository;
import pluto.upik.shared.cache.CacheNames;
import pluto.upik.shared.exception.BadWordException;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.filter.BadWordFilterService;
import pluto.upik.shared.oauth2jwt.entity.User;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService implements BoardServiceInterface {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BadWordFilterService badWordFilterService;
    private final pluto.upik.domain.bookmark.repository.BoardBookmarkRepository boardBookmarkRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.BOARD_LIST, key = "T(java.lang.String).format('%s:%d:%d:%s', #currentUserId, #page, #size, #sortBy)")
    public BoardPage getQuestionList(int page, int size, BoardSortType sortBy, UUID currentUserId) {
        Page<Board> boardPage;

        if (sortBy == BoardSortType.POPULAR) {
            // 인기순 정렬 (댓글 많은 순)
            Pageable pageable = PageRequest.of(page, size);
            boardPage = boardRepository.findAllOrderByCommentCount(pageable);
        } else {
            // 시간순 정렬 (최신순) - 기본값
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            boardPage = boardRepository.findAll(pageable);
        }

        List<BoardResponse> boardResponses = boardPage.getContent().stream()
                .map(board -> mapBoardToBoardResponse(board, currentUserId))
                .collect(Collectors.toList());

        return BoardPage.builder()
                .content(boardResponses)
                .totalPages(boardPage.getTotalPages())
                .totalElements(boardPage.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.BOARD_USER, key = "T(java.lang.String).format('%s:%d:%d', #userId, #page, #size)")
    public BoardPage getMyQuestions(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Board> boardPage = boardRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<BoardResponse> boardResponses = boardPage.getContent().stream()
                .map(board -> mapBoardToBoardResponse(board, userId))
                .collect(Collectors.toList());

        return BoardPage.builder()
                .content(boardResponses)
                .totalPages(boardPage.getTotalPages())
                .totalElements(boardPage.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.BOARD_SEARCH, key = "T(java.lang.String).format('%s:%s:%d:%d', #currentUserId, #keyword, #page, #size)")
    public BoardPage searchQuestions(String keyword, int page, int size, UUID currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Board> boardPage = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        
        List<BoardResponse> boardResponses = boardPage.getContent().stream()
                .map(board -> mapBoardToBoardResponse(board, currentUserId))
                .collect(Collectors.toList());
        
        return BoardPage.builder()
                .content(boardResponses)
                .totalPages(boardPage.getTotalPages())
                .totalElements(boardPage.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public BoardResponse getQuestionDetail(UUID boardId, UUID currentUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 게시글입니다."));
        
        // 조회수 증가
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
        
        return mapBoardToBoardResponse(board, currentUserId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.BOARD_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_USER, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_COMMENTS, allEntries = true)
    })
    public BoardResponse createQuestion(CreateBoardInput input, UUID userId) {
        // 욕설 검증
        if (badWordFilterService.containsBadWord(input.getTitle())) {
            throw BadWordException.Predefined.inTitle();
        }
        if (badWordFilterService.containsBadWord(input.getContent())) {
            throw BadWordException.Predefined.inContent();
        }

        Board board = new Board();
        board.setId(UUID.randomUUID());
        board.setTitle(input.getTitle());
        board.setContent(input.getContent());
        board.setUserId(userId);
        board.setViewCount(0);
        board.setCreatedAt(LocalDateTime.now());
        board.setUpdatedAt(LocalDateTime.now());

        Board savedBoard = boardRepository.save(board);
        return mapBoardToBoardResponse(savedBoard, userId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.BOARD_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_USER, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_COMMENTS, allEntries = true)
    })
    public BoardResponse updateQuestion(UUID boardId, UpdateBoardInput input, UUID userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 게시글입니다."));

        if (!board.getUserId().equals(userId)) {
            throw new BusinessException("게시글 수정 권한이 없습니다.");
        }

        // 욕설 검증
        if (badWordFilterService.containsBadWord(input.getTitle())) {
            throw BadWordException.Predefined.inTitle();
        }
        if (badWordFilterService.containsBadWord(input.getContent())) {
            throw BadWordException.Predefined.inContent();
        }

        board.setTitle(input.getTitle());
        board.setContent(input.getContent());
        board.setUpdatedAt(LocalDateTime.now());

        Board updatedBoard = boardRepository.save(board);
        return mapBoardToBoardResponse(updatedBoard, userId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.BOARD_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_USER, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_COMMENTS, allEntries = true)
    })
    public boolean deleteQuestion(UUID boardId, UUID userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 게시글입니다."));
        
        if (!board.getUserId().equals(userId)) {
            throw new BusinessException("게시글 삭제 권한이 없습니다.");
        }
        
        // 게시글에 달린 댓글도 모두 삭제
        commentRepository.deleteByBoardId(boardId);
        boardRepository.delete(board);
        
        return true;
    }

    @Override
    @Transactional
    public boolean toggleBoardBookmark(UUID userId, UUID boardId) {
        Optional<BoardBookmark> existing = boardBookmarkRepository.findByUserIdAndBoardId(userId, boardId);
        if (existing.isPresent()) {
            boardBookmarkRepository.delete(existing.get());
            return false;
        }

        BoardBookmark bookmark = BoardBookmark.builder()
                .userId(userId)
                .boardId(boardId)
                .build();
        boardBookmarkRepository.save(bookmark);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBoardBookmarked(UUID userId, UUID boardId) {
        return boardBookmarkRepository.existsByUserIdAndBoardId(userId, boardId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getBoardBookmarkCount(UUID boardId) {
        return boardBookmarkRepository.countByBoardId(boardId);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardPage getBookmarkedQuestions(UUID userId, int page, int size) {
        List<UUID> boardIds = boardBookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(pluto.upik.domain.bookmark.data.model.BoardBookmark::getBoardId)
                .collect(Collectors.toList());

        if (boardIds.isEmpty()) {
            return BoardPage.builder()
                    .content(List.of())
                    .totalElements(0)
                    .totalPages(0)
                    .build();
        }

        List<Board> boards = boardRepository.findAllById(boardIds)
                .stream()
                .sorted(Comparator.comparingInt(boardIds::indexOf))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());

        List<BoardResponse> content = boards.stream()
                .map(board -> mapBoardToBoardResponse(board, userId))
                .collect(Collectors.toList());

        int totalElements = boardIds.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return BoardPage.builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .build();
    }
    
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheNames.BOARD_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_USER, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.BOARD_COMMENTS, allEntries = true)
    })
    public CommentResponse createComment(CreateCommentInput input, UUID userId) {
        // 욕설 검증
        if (badWordFilterService.containsBadWord(input.getContent())) {
            throw BadWordException.Predefined.inComment();
        }

        // 게시글 존재 여부 확인
        boardRepository.findById(input.getBoardId())
                .orElseThrow(() -> new BusinessException("존재하지 않는 게시글입니다."));

        // 부모 댓글이 있는 경우 존재 여부 확인
        if (input.getParentId() != null) {
            commentRepository.findById(input.getParentId())
                    .orElseThrow(() -> new BusinessException("존재하지 않는 댓글입니다."));
        }

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setContent(input.getContent());
        comment.setUserId(userId);
        comment.setBoardId(input.getBoardId());
        comment.setParentId(input.getParentId());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return mapCommentToCommentResponse(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.BOARD_COMMENTS, key = "T(java.lang.String).format('%s:%d:%d', #boardId, #page, #size)")
    public CommentPage getComments(UUID boardId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        
        // 부모 댓글만 먼저 조회
        Page<Comment> parentCommentPage = commentRepository.findByBoardIdAndParentIdIsNull(boardId, pageable);
        
        List<CommentResponse> commentResponses = parentCommentPage.getContent().stream()
                .map(parentComment -> {
                    CommentResponse parentResponse = mapCommentToCommentResponse(parentComment);
                    
                    // 각 부모 댓글의 자식 댓글 조회
                    List<Comment> childComments = commentRepository.findByParentId(parentComment.getId());
                    List<CommentResponse> childResponses = childComments.stream()
                            .map(this::mapCommentToCommentResponse)
                            .collect(Collectors.toList());
                    
                    parentResponse.setReplies(childResponses);
                    return parentResponse;
                })
                .collect(Collectors.toList());
        
        return CommentPage.builder()
                .content(commentResponses)
                .totalPages(parentCommentPage.getTotalPages())
                .totalElements(parentCommentPage.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public boolean reportBoard(UUID boardId, String reason, String detail, UUID reporterId) {
        // 게시글 존재 여부 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 게시글입니다."));
        
        // 신고 정보 저장 로직 (별도의 신고 서비스로 위임하거나 직접 구현)
        log.info("게시글 신고 접수: boardId={}, reason={}, reporterId={}", boardId, reason, reporterId);
        
        // 실제 구현에서는 신고 정보를 데이터베이스에 저장하는 로직 필요
        return true;
    }

    @Override
    @Transactional
    public boolean reportComment(UUID commentId, String reason, String detail, UUID reporterId) {
        // 댓글 존재 여부 확인
        commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 댓글입니다."));
        
        // 신고 정보 저장 로직 (별도의 신고 서비스로 위임하거나 직접 구현)
        log.info("댓글 신고 접수: commentId={}, reason={}, reporterId={}", commentId, reason, reporterId);
        
        // 실제 구현에서는 신고 정보를 데이터베이스에 저장하는 로직 필요
        return true;
    }
    
    // Board 엔티티를 BoardResponse DTO로 변환하는 메소드
    private BoardResponse mapBoardToBoardResponse(Board board, UUID currentUserId) {
        long commentCount = commentRepository.countByBoardId(board.getId());
        long bookmarkCount = boardBookmarkRepository.countByBoardId(board.getId());
        
        // 사용자 정보 조회
        String userName = "Unknown User"; // 기본값 설정

        try {
            Optional<User> userOpt = userRepository.findById(board.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userName = user.getName(); // 데이터베이스의 name 필드 사용
            } else {
                log.warn("사용자를 찾을 수 없음: userId={}", board.getUserId());
            }
        } catch (Exception e) {
            log.warn("사용자 정보 조회 중 오류 발생: userId={}", board.getUserId(), e);
        }

        boolean isBookmarked = currentUserId != null && boardBookmarkRepository.existsByUserIdAndBoardId(currentUserId, board.getId());
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUserId())
                .userName(userName) // 조회한 사용자 이름 설정
                .viewCount(board.getViewCount())
                .commentCount(commentCount)
                .bookmarkCount(bookmarkCount)
                .isBookmarked(isBookmarked)
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
    
    // Comment 엔티티를 CommentResponse DTO로 변환하는 메소드
    private CommentResponse mapCommentToCommentResponse(Comment comment) {
        // 사용자 정보 조회
        String userName = "Unknown User"; // 기본값 설정

        try {
            Optional<User> userOpt = userRepository.findById(comment.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userName = user.getName(); // 데이터베이스의 name 필드 사용
            } else {
                log.warn("댓글 작성자를 찾을 수 없음: userId={}", comment.getUserId());
            }
        } catch (Exception e) {
            log.warn("댓글 작성자 정보 조회 중 오류 발생: userId={}", comment.getUserId(), e);
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .userName(userName) // 조회한 사용자 이름 설정
                .boardId(comment.getBoardId())
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(List.of()) // 기본적으로 빈 리스트로 초기화
                .build();
    }
}
