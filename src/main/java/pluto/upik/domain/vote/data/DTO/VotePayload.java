package pluto.upik.domain.vote.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.vote.data.model.Vote;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotePayload implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;  // 스키마와 일치하도록 question -> title로 변경
    private String category;
    private String finishedAt;
    private String status;
    private String closureType; // 투표 종료 타입
    private Integer participantThreshold; // 참여자 수 기준 (PARTICIPANT_COUNT 타입일 때만)
    private int totalResponses;
    private List<OptionWithStatsPayload> options;
    private boolean hasVoted; // 사용자가 투표에 참여했는지 여부

    public boolean getHasVoted() {
        return this.hasVoted;
    }

    /**
     * Vote 엔티티와 옵션 목록을 기반으로 옵션 통계 없이 VotePayload 객체를 생성합니다.
     *
     * 옵션 통계는 모두 0으로 초기화되며, 총 응답 수와 hasVoted 필드는 각각 0과 false로 설정됩니다.
     *
     * @param vote   투표 엔티티
     * @param options 투표에 포함된 옵션 목록
     * @return 옵션 통계가 없는 VotePayload 인스턴스
     */
    public static VotePayload fromEntity(Vote vote, List<Option> options) {
        return VotePayload.builder()
                .id(vote.getId().toString())
                .title(vote.getQuestion())  // question 필드를 title로 매핑
                .category(vote.getCategory())
                .finishedAt(vote.getFinishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .status(vote.getStatus().name())
                .closureType(vote.getClosureType() != null ? vote.getClosureType().name() : Vote.ClosureType.DEFAULT.name())
                .participantThreshold(vote.getParticipantThreshold())
                .totalResponses(0) // 기본값
                .options(options.stream()
                        .map(option -> new OptionWithStatsPayload(option.getId().toString(), option.getContent(), 0, 0))
                        .toList())
                .hasVoted(false) // 기본값
                .build();
    }

    /**
     * 옵션 통계 정보를 포함하여 Vote 엔티티로부터 VotePayload 객체를 생성합니다.
     *
     * 옵션 통계와 총 응답 수를 포함하며, 사용자의 투표 여부는 기본값(false)로 설정됩니다.
     *
     * @param vote 투표 엔티티
     * @param options 투표 옵션 목록
     * @param optionStats 각 옵션의 통계 정보 목록
     * @param totalResponses 전체 응답 수
     * @return 옵션 통계와 응답 수가 포함된 VotePayload 객체
     */
    public static VotePayload fromEntityWithStats(Vote vote, List<Option> options,
                                                 List<OptionWithStatsPayload> optionStats, int totalResponses) {
        return fromEntityWithStats(vote, options, optionStats, totalResponses, false);
    }
    
    /**
     * Vote 엔티티와 옵션, 옵션 통계, 총 응답 수, 사용자의 투표 여부 정보를 기반으로 VotePayload 객체를 생성합니다.
     *
     * @param vote       투표 엔티티
     * @param options    투표에 포함된 옵션 목록
     * @param optionStats 각 옵션의 통계 정보가 포함된 페이로드 목록
     * @param totalResponses 총 응답 수
     * @param hasVoted   사용자가 해당 투표에 참여했는지 여부
     * @return           투표 상세 정보와 통계, 참여 여부가 포함된 VotePayload 객체
     */
    public static VotePayload fromEntityWithStats(Vote vote, List<Option> options,
                                                 List<OptionWithStatsPayload> optionStats, int totalResponses,
                                                 boolean hasVoted) {
        return VotePayload.builder()
                .id(vote.getId().toString())
                .title(vote.getQuestion())
                .category(vote.getCategory())
                .finishedAt(vote.getFinishedAt().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .status(vote.getStatus().name())
                .closureType(vote.getClosureType() != null ? vote.getClosureType().name() : Vote.ClosureType.DEFAULT.name())
                .participantThreshold(vote.getParticipantThreshold())
                .totalResponses(totalResponses)
                .options(optionStats)
                .hasVoted(hasVoted)
                .build();
    }
}
