package pluto.upik.domain.board.data.DTO;

/**
 * 게시판 정렬 타입
 * - CHRONOLOGICAL: 시간순 정렬 (최신순)
 * - POPULAR: 인기순 정렬 (댓글 많은 순)
 */
public enum BoardSortType {
    CHRONOLOGICAL,  // 시간순 (최신순)
    POPULAR         // 인기순 (댓글 많은 순)
}
