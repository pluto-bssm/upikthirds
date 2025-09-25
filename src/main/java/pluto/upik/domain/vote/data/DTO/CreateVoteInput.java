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
    
    // 투표 종료 일자 (기본값: 7일 후)
    private LocalDate finishDate;
    
    // 참여자 수 기준 종료 조건 (null이면 적용 안 함)
    private Integer participantThreshold;
}