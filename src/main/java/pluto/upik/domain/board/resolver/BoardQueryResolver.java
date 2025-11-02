package pluto.upik.domain.board.resolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import pluto.upik.domain.board.data.DTO.BoardPage;
import pluto.upik.domain.board.data.DTO.BoardQuery;
import pluto.upik.domain.board.data.DTO.BoardResponse;
import pluto.upik.domain.board.data.DTO.BoardSortType;
import pluto.upik.domain.board.data.DTO.CommentPage;
import pluto.upik.domain.board.service.BoardServiceInterface;
import pluto.upik.shared.oauth2jwt.util.SecurityUtil;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardQueryResolver {

    private final BoardServiceInterface boardService;
    private final SecurityUtil securityUtil;

    @SchemaMapping(typeName = "BoardQuery", field = "getQuestionList")
    public BoardPage getQuestionList(BoardQuery parent, @Argument int page, @Argument int size, @Argument(name = "sortBy") BoardSortType sortBy) {
        try {
            // sortBy가 null이면 기본값으로 CHRONOLOGICAL 사용
            BoardSortType sort = (sortBy != null) ? sortBy : BoardSortType.CHRONOLOGICAL;
            return boardService.getQuestionList(page, size, sort);
        } catch (Exception e) {
            log.error("질문 목록 조회 중 오류 발생", e);
            throw new RuntimeException("질문 목록을 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    @SchemaMapping(typeName = "BoardQuery", field = "searchQuestions")
    public BoardPage searchQuestions(BoardQuery parent, @Argument String keyword, @Argument int page, @Argument int size) {
        try {
            return boardService.searchQuestions(keyword, page, size);
        } catch (Exception e) {
            log.error("질문 검색 중 오류 발생: keyword={}", keyword, e);
            throw new RuntimeException("질문을 검색하는 중 오류가 발생했습니다.", e);
        }
    }

    @SchemaMapping(typeName = "BoardQuery", field = "getQuestionDetail")
    public BoardResponse getQuestionDetail(BoardQuery parent, @Argument String boardId) {
        try {
            UUID boardUuid = UUID.fromString(boardId);
            return boardService.getQuestionDetail(boardUuid);
        } catch (Exception e) {
            log.error("질문 상세 조회 중 오류 발생: boardId={}", boardId, e);
            throw new RuntimeException("질문 상세 정보를 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    @SchemaMapping(typeName = "BoardQuery", field = "getComments")
    public CommentPage getComments(BoardQuery parent, @Argument String boardId, @Argument int page, @Argument int size) {
        try {
            UUID boardUuid = UUID.fromString(boardId);
            return boardService.getComments(boardUuid, page, size);
        } catch (Exception e) {
            log.error("댓글 목록 조회 중 오류 발생: boardId={}", boardId, e);
            throw new RuntimeException("댓글 목록을 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    @SchemaMapping(typeName = "BoardQuery", field = "getMyQuestions")
    public BoardPage getMyQuestions(BoardQuery parent, @Argument int page, @Argument int size) {
        try {
            UUID currentUserId = securityUtil.getCurrentUserId();
            return boardService.getMyQuestions(currentUserId, page, size);
        } catch (Exception e) {
            log.error("내가 작성한 질문 목록 조회 중 오류 발생", e);
            throw new RuntimeException("내가 작성한 질문 목록을 조회하는 중 오류가 발생했습니다.", e);
        }
    }
}