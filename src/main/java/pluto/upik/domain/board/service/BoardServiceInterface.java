package pluto.upik.domain.board.service;

import pluto.upik.domain.board.data.DTO.*;
import pluto.upik.domain.board.data.model.Board;
import pluto.upik.domain.board.data.model.Comment;

import java.util.UUID;

public interface BoardServiceInterface {
    // 질문 리스트 조회
    BoardPage getQuestionList(int page, int size, BoardSortType sortBy);

    // 내가 작성한 질문 리스트 조회
    BoardPage getMyQuestions(UUID userId, int page, int size);

    // 질문 검색
    BoardPage searchQuestions(String keyword, int page, int size);
    
    // 질문 상세 조회
    BoardResponse getQuestionDetail(UUID boardId);
    
    // 질문 작성
    BoardResponse createQuestion(CreateBoardInput input, UUID userId);
    
    // 질문 수정
    BoardResponse updateQuestion(UUID boardId, UpdateBoardInput input, UUID userId);
    
    // 질문 삭제
    boolean deleteQuestion(UUID boardId, UUID userId);
    
    // 댓글 작성
    CommentResponse createComment(CreateCommentInput input, UUID userId);
    
    // 댓글 리스트 조회
    CommentPage getComments(UUID boardId, int page, int size);
    
    // 게시글 신고
    boolean reportBoard(UUID boardId, String reason, String detail, UUID reporterId);
    
    // 댓글 신고
    boolean reportComment(UUID commentId, String reason, String detail, UUID reporterId);
}