package pluto.upik.domain.vote.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoteInput {
    private String title;
    private String category;
    private List<String> options;

    /**
     * 투표 종료 타입
     * DEFAULT: 기본 7일 후 종료
     * CUSTOM_DAYS: 특정 일수 후 종료 (customDays 필드 필수)
     * PARTICIPANT_COUNT: 참여자 수 기준 종료 (participantThreshold 필드 필수)
     */
    private String closureType;

    /**
     * 커스텀 종료 일수
     * closureType이 CUSTOM_DAYS일 때 필수
     * 예: 3이면 3일 후 종료, 14이면 14일 후 종료
     */
    private Integer customDays;

    /**
     * 참여자 수 기준 종료 조건
     * closureType이 PARTICIPANT_COUNT일 때 필수
     * 예: 100이면 100명 참여 시 자동 종료
     */
    private Integer participantThreshold;
}