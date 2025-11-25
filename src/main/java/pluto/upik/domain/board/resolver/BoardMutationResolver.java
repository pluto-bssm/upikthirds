package pluto.upik.domain.board.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.board.data.DTO.*;
import pluto.upik.domain.board.service.BoardServiceInterface;
import pluto.upik.shared.oauth2jwt.annotation.RequireAuth;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardMutationResolver {

    private final BoardServiceInterface boardService;
    private final SecurityUtil securityUtil;

    @RequireAuth
    @SchemaMapping(typeName = "BoardMutation", field = "createQuestion")
    public BoardResponse createQuestion(BoardMutation parent, @Argument CreateBoardInput input) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            return boardService.createQuestion(input, userId);
        } catch (Exception e) {
            log.error("질문 작성 중 오류 발생", e);
            throw new RuntimeException("질문을 작성하는 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "BoardMutation", field = "updateQuestion")
    public BoardResponse updateQuestion(BoardMutation parent, @Argument String boardId, @Argument UpdateBoardInput input) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID boardUuid = UUID.fromString(boardId);
            return boardService.updateQuestion(boardUuid, input, userId);
        } catch (Exception e) {
            log.error("질문 수정 중 오류 발생: boardId={}", boardId, e);
            throw new RuntimeException("질문을 수정하는 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "BoardMutation", field = "deleteQuestion")
    public boolean deleteQuestion(BoardMutation parent, @Argument String boardId) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID boardUuid = UUID.fromString(boardId);
            return boardService.deleteQuestion(boardUuid, userId);
        } catch (Exception e) {
            log.error("질문 삭제 중 오류 발생: boardId={}", boardId, e);
            throw new RuntimeException("질문을 삭제하는 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "BoardMutation", field = "createComment")
    public CommentResponse createComment(BoardMutation parent, @Argument CreateCommentInput input) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            return boardService.createComment(input, userId);
        } catch (Exception e) {
            log.error("댓글 작성 중 오류 발생", e);
            throw new RuntimeException("댓글을 작성하는 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "BoardMutation", field = "reportBoard")
    public boolean reportBoard(BoardMutation parent, @Argument String boardId, @Argument String reason, @Argument String detail) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID boardUuid = UUID.fromString(boardId);
            return boardService.reportBoard(boardUuid, reason, detail, userId);
        } catch (Exception e) {
            log.error("게시글 신고 중 오류 발생: boardId={}", boardId, e);
            throw new RuntimeException("게시글을 신고하는 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "BoardMutation", field = "reportComment")
    public boolean reportComment(BoardMutation parent, @Argument String commentId, @Argument String reason, @Argument String detail) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID commentUuid = UUID.fromString(commentId);
            return boardService.reportComment(commentUuid, reason, detail, userId);
        } catch (Exception e) {
            log.error("댓글 신고 중 오류 발생: commentId={}", commentId, e);
            throw new RuntimeException("댓글을 신고하는 중 오류가 발생했습니다.", e);
        }
    }

    @RequireAuth
    @SchemaMapping(typeName = "BoardMutation", field = "toggleBoardBookmark")
    public boolean toggleBoardBookmark(BoardMutation parent, @Argument String boardId) {
        try {
            UUID userId = securityUtil.getCurrentUserId();
            UUID boardUuid = UUID.fromString(boardId);
            return boardService.toggleBoardBookmark(userId, boardUuid);
        } catch (Exception e) {
            log.error("질문 북마크 토글 중 오류 발생: boardId={}", boardId, e);
            throw new RuntimeException("질문 북마크 처리 중 오류가 발생했습니다.", e);
        }
    }
}
