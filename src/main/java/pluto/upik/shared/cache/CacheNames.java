package pluto.upik.shared.cache;

/**
 * 중앙에서 캐시 이름을 관리하여 오타를 방지하고, 서비스 간 일관성을 유지합니다.
 */
public final class CacheNames {

    private CacheNames() {
    }

    // Board
    public static final String BOARD_LIST = "board:list";
    public static final String BOARD_USER = "board:user";
    public static final String BOARD_SEARCH = "board:search";
    public static final String BOARD_COMMENTS = "board:comments";

    // Vote
    public static final String VOTE_LIST = "vote:list";
    public static final String VOTE_DETAIL = "vote:detail";
    public static final String VOTE_POPULAR = "vote:popular";
    public static final String VOTE_LEAST = "vote:least";
    public static final String VOTE_MY = "vote:my";
}
