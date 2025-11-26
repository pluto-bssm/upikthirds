package pluto.upik.domain.vote.data.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteDetailPayload implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String category;
    private String status;
    private String createdBy;
    private String finishedAt;
    private String closureType; // 투표 종료 타입
    private Integer participantThreshold; // 참여자 수 기준
    private int totalResponses;
    private List<OptionWithStatsPayload> options;
    private boolean hasVoted; // 사용자가 투표에 참여했는지 여부
    private String myOptionId; // 사용자가 선택한 옵션 ID
    private String myOptionContent; // 사용자가 선택한 옵션 내용
    private String tailId; // 투표에 등록된 꼬리 질문 ID
    private String tailQuestion; // 투표에 등록된 꼬리 질문 내용
    private String myTailId; // 사용자가 답변한 꼬리 질문 ID
    private String myTailQuestion; // 사용자가 답변한 꼬리 질문 내용
    private String myTailAnswer; // 사용자가 답변한 꼬리 질문 텍스트
}
