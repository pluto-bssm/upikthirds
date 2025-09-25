package pluto.upik.shared.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pluto.upik.domain.guide.data.model.Guide;
import pluto.upik.domain.guide.repository.GuideRepository;
import pluto.upik.domain.option.data.model.Option;
import pluto.upik.domain.option.repository.OptionRepository;
import pluto.upik.domain.tail.data.model.Tail;
import pluto.upik.domain.tail.repository.TailRepository;
import pluto.upik.domain.tail.repository.TailResponseRepository;
import pluto.upik.domain.vote.data.model.Vote;
import pluto.upik.domain.vote.repository.VoteRepository;
import pluto.upik.domain.voteResponse.data.model.VoteResponse;
import pluto.upik.domain.voteResponse.repository.VoteResponseRepository;
import pluto.upik.shared.ai.config.ChatAiService;
import pluto.upik.shared.ai.data.DTO.GuideResponseDTO;
import pluto.upik.shared.exception.BusinessException;
import pluto.upik.shared.exception.ResourceNotFoundException;
import pluto.upik.shared.oauth2jwt.repository.UserRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService implements DisposableBean {
    private static final int MAX_CHUNK_SIZE = 450;

    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteResponseRepository voteResponseRepository;
    private final OptionRepository optionRepository;
    private final TailRepository tailRepository;
    private final TailResponseRepository tailResponseRepository;
    private final ChatAiService chatAiService;
    private final GuideRepository guideRepository;

    // 현재 진행 중인 AI 요청을 추적하기 위한 맵 (요청 ID -> 취소 플래그)
    private final Map<String, AtomicBoolean> activeRequests = new ConcurrentHashMap<>();

    // 전역 취소 플래그 (서버 종료 시 사용)
    private final AtomicBoolean globalCancellationFlag = new AtomicBoolean(false);

    /**
     * 입력 문자열에서 `<think>...</think>` 태그와 그 내부 내용을 모두 제거한 후, 앞뒤 공백을 제거하여 반환합니다.
     *
     * @param response AI 응답 문자열
     * @return `<think>` 태그가 제거된 문자열, 입력이 null이면 null 반환
     */
    private String removeThinkTags(String response) {
        if (response == null) return null;
        return response.replaceAll("(?is)<think>.*?</think>", "").trim();
    }

    /**
     * 지정된 요청 키로 새로운 요청 처리를 시작하며, 동일 키의 이전 요청이 있으면 취소합니다.
     *
     * @param requestKey 요청을 식별하는 고유 키
     * @return 동일 키의 이전 요청이 이미 진행 중이었다면 true, 아니면 false
     */
    public boolean startRequest(String requestKey) {
        // 이전 요청이 있으면 취소
        boolean hadPreviousRequest = false;
        if (activeRequests.containsKey(requestKey)) {
            hadPreviousRequest = true;
            AtomicBoolean flag = activeRequests.get(requestKey);
            if (flag != null) {
                flag.set(true); // 취소 플래그 설정
                log.info("이전 요청 취소: {}", requestKey);
            }
        }

        // 새 요청 등록
        activeRequests.put(requestKey, new AtomicBoolean(false));
        log.info("새 요청 시작: {}", requestKey);
        return hadPreviousRequest;
    }

    /**
 * 지정된 요청 키에 해당하는 클라이언트 요청의 추적을 종료합니다.
 *
 * @param requestKey 종료할 요청을 식별하는 고유 키
 */
    public void endRequest(String requestKey) {
        activeRequests.remove(requestKey);
        log.info("요청 종료: {}", requestKey);
}

    /**
     * 지정된 요청 키에 해당하는 요청을 취소 상태로 표시하여, 이후 처리 중단을 유도합니다.
     *
     * @param requestKey 취소할 요청을 식별하는 고유 키
     */
    public void markClientDisconnected(String requestKey) {
        AtomicBoolean flag = activeRequests.get(requestKey);
        if (flag != null) {
            flag.set(true);
            log.info("클라이언트 연결 끊김 감지: {}", requestKey);
        }
    }

    /**
     * 주어진 요청 키에 해당하는 작업이 취소되었거나 서버가 종료 중인지 확인합니다.
     *
     * @param requestKey 요청을 식별하는 고유 키
     * @throws BusinessException 요청이 취소되었거나 서버가 종료 중인 경우 예외가 발생합니다.
     */
    private void checkCancellation(String requestKey) {
        // 서버 종료 확인
        if (globalCancellationFlag.get()) {
            log.info("서버 종료로 인한 작업 취소");
            throw new BusinessException("서버 종료로 인한 작업 취소");
        }

        // 요청 취소 확인
        AtomicBoolean flag = activeRequests.get(requestKey);
        if (flag == null || flag.get()) {
            log.info("요청이 취소되었습니다: {}", requestKey);
            throw new BusinessException("요청이 취소되었습니다");
        }
    }

    /**
     * AI에게 질문을 전송하고 응답을 반환합니다.
     *
     * AI 서비스에 질문을 전달하고, 응답에서 불필요한 태그를 제거하여 반환합니다.
     * 요청이 취소되었거나 서버가 종료 중인 경우 예외가 발생할 수 있습니다.
     *
     * @param question AI에게 전달할 질문
     * @param requestKey 요청을 식별하는 고유 키
     * @return AI의 응답 문자열
     */
    private String askToDeepSeekAI(String question, String requestKey) {
        try {
            log.info("AI 요청 시작: {}", requestKey);

            // 취소 여부 확인
            checkCancellation(requestKey);

            // AI에게 질문
            log.info("AI 호출 시작: {}", requestKey);
            String response = chatAiService.askToDeepSeekAI(question);
            log.info("AI 호출 완료: {}", requestKey);

            // 취소 여부 다시 확인
            checkCancellation(requestKey);

            // 응답 처리
            String result = removeThinkTags(response);
            log.info("AI 응답 처리 완료: {}", requestKey);
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI 서비스 호출 중 오류: {}", e.getMessage(), e);
            throw new BusinessException("AI 서비스 호출 중 오류가 발생했습니다.");
        }
    }

    /**
     * 주어진 투표 ID와 가이드 유형을 기반으로 AI를 활용해 가이드를 생성하고 저장한 후, 결과를 GuideResponseDTO로 반환합니다.
     *
     * @param voteId 가이드를 생성할 투표의 ID
     * @param type 생성할 가이드의 유형
     * @return 생성된 가이드의 정보를 담은 GuideResponseDTO
     * @throws ResourceNotFoundException 투표, 옵션, 꼬리 질문이 존재하지 않을 경우 발생
     * @throws BusinessException AI 응답 포맷이 예상과 다르거나, 처리 중 오류 또는 취소가 발생한 경우 발생
     */
    @Transactional
    public GuideResponseDTO generateAndSaveGuide(UUID voteId, String type) {
        // 요청 키 생성 (voteId와 type으로 구성)
        String requestKey = voteId.toString() + "-" + type;

        try {
            // 요청 시작
            startRequest(requestKey);

            Vote vote = voteRepository.findById(voteId)
                    .orElseThrow(() -> new ResourceNotFoundException("투표를 찾을 수 없습니다."));

            vote.setStatus(Vote.Status.valueOf("CLOSED"));

            String voteTitle = vote.getQuestion();
            String voteDescription = optionRepository.findTopByVoteOrderByIdAsc(vote)
                    .map(Option::getContent)
                    .orElse("설명 없음");

            // 취소 여부 확인
            checkCancellation(requestKey);

            // voteId로 실제 투표 옵션 및 응답을 가져와서 퍼센트 계산
            List<Option> options = optionRepository.findByVoteId(vote.getId());
            if (options == null || options.isEmpty()) {
                throw new ResourceNotFoundException("투표 옵션이 존재하지 않습니다.");
            }
            List<VoteResponse> voteResponses = voteResponseRepository.findByVoteId(vote.getId());

            Map<UUID, Long> voteCounts = new HashMap<>();
            long totalVotes = voteResponses.size();

            for (VoteResponse vr : voteResponses) {
                voteCounts.merge(vr.getSelectedOption().getId(), 1L, Long::sum);
            }

            StringBuilder optionPercentsBuilder = new StringBuilder();
            for (Option option : options) {
                long count = voteCounts.getOrDefault(option.getId(), 0L);
                double percent = totalVotes > 0 ? (count * 100.0 / totalVotes) : 0.0;
                optionPercentsBuilder
                        .append(option.getContent())
                        .append(" - ")
                        .append(String.format("%.1f", percent))
                        .append("%\n");
            }
            String optionsWithPercents = optionPercentsBuilder.toString().trim();

            // 취소 여부 확인
            checkCancellation(requestKey);

            Tail tail = tailRepository.findFirstByVote(vote)
                    .orElseThrow(() -> new ResourceNotFoundException("Tail 질문이 없습니다."));

            List<String> tailAnswers = tailResponseRepository.findByTail(tail).stream()
                    .map(tr -> tr.getAnswer())
                    .toList();

            String tailResponses = String.join("\n", tailAnswers);

            String prompt = String.format(
                    "다음 투표와 응답에 대한 가이드 제목과 내용을 생성해주세요. 각 선택지에는 '\\' 같은 것을 넣지 말고 일반 텍스트로만 제공해주세요. " +
                    "가이드는 명확하고 정보가 풍부하며 심층적이어야 합니다.\n\n" +
                    "투표 제목: %s\n" +
                    "가장 많은 표를 받은 옵션: %s\n" +
                    "투표 결과(백분율):\n%s\n\n" +
                    "꼬리 질문: %s\n" +
                    "꼬리 응답들:\n%s\n\n" +
                    "다음과 같은 형식으로 작성해주세요:\n%s\n\n" +
                    "다음 형식으로 결과를 반환해주세요:\n" +
                    "가이드 제목:\n<<제목>>\n\n" +
                    "가이드 내용:\n<<내용>>\n",
                    voteTitle, voteDescription, optionsWithPercents,
                    tail.getQuestion(), tailResponses, type
            );

            // 취소 여부 확인
            checkCancellation(requestKey);

            // AI 요청
            String result = askToDeepSeekAI(prompt, requestKey);

            // 취소 여부 확인
            checkCancellation(requestKey);

            int titleStart = result.indexOf("가이드 제목:");
            int contentStart = result.indexOf("가이드 내용:");

            if (titleStart == -1 || contentStart == -1) {
                // 영어로 응답했을 경우를 대비해 영어 형식도 확인
                titleStart = result.indexOf("Guide Title:");
                contentStart = result.indexOf("Guide Content:");

                if (titleStart == -1 || contentStart == -1) {
                log.error("AI 응답 포맷이 예상과 다릅니다. result: {}", result);
                throw new BusinessException("AI 응답 포맷이 예상과 다릅니다.");
            }
            }

            String extractedTitle = result.substring(titleStart + (result.contains("가이드 제목:") ? "가이드 제목:".length() : "Guide Title:".length()), contentStart).trim();
            String extractedContent = result.substring(contentStart + (result.contains("가이드 내용:") ? "가이드 내용:".length() : "Guide Content:".length())).trim();

            // 취소 여부 확인
            checkCancellation(requestKey);

            Guide guide = Guide.builder()
                    .vote(vote)
                    .title(extractedTitle)
                    .content(extractedContent)
                    .createdAt(LocalDate.now())
                    .category(vote.getCategory())
                    .guideType(type)
                    .revoteCount(0L)
                    .like(0L)
                    .build();

            guideRepository.save(guide);

            // GuideResponseDTO 형식으로 반환
            return GuideResponseDTO.builder()
                    .id(guide.getId())
                    .voteId(guide.getVote().getId())
                    .title(guide.getTitle())
                    .content(guide.getContent())
                    .createdAt(guide.getCreatedAt())
                    .category(guide.getCategory())
                    .guideType(type)
                    .revoteCount(guide.getRevoteCount())
                    .like(guide.getLike())
                    .build();
        } catch (ResourceNotFoundException | BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("가이드 생성 중 알 수 없는 오류: {}", e.getMessage(), e);
            throw new BusinessException("가이드 생성 중 오류가 발생했습니다.");
        } finally {
            // 요청 종료
            endRequest(requestKey);
        }
    }

    /**
     * 주어진 질문에 대한 투표 선지 옵션을 AI를 통해 생성합니다.
     *
     * @param question 투표 질문
     * @param requestKey 요청을 식별하는 고유 키
     * @return 생성된 선지 옵션 목록
     */
    public List<String> generateVoteOptions(String question, String requestKey) {
        try {
            // 요청 시작
            startRequest(requestKey);

            // 취소 여부 확인
            checkCancellation(requestKey);

            String prompt = String.format(
                "다음 투표 질문에 대한 4-6개의 합리적인 선택지를 생성해주세요. " +
                "선택지는 다양하고 관련성이 있으며 가장 가능성 있는 답변들을 포함해야 합니다. " +
                "번호 매기기나 추가 텍스트 없이 한 줄에 하나씩 선택지 목록만 반환해주세요.\n\n" +
                "투표 질문: %s\n\n",
                question
            );

            // AI 요청
            String result = askToDeepSeekAI(prompt, requestKey);

            // 취소 여부 확인
            checkCancellation(requestKey);

            // 결과 파싱
            List<String> options = Arrays.stream(result.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

            return options;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("선지 옵션 생성 중 오류: {}", e.getMessage(), e);
            throw new BusinessException("선지 옵션 생성 중 오류가 발생했습니다.");
        } finally {
            // 요청 종료
            endRequest(requestKey);
        }
    }

    /**
     * 서버 종료 시 호출되어 모든 진행 중인 AI 요청을 취소 상태로 전환합니다.
     *
     * 서비스의 전역 취소 플래그를 활성화하여 이후의 요청 및 현재 진행 중인 요청이 중단되도록 합니다.
     */
    @Override
    public void destroy() {
        log.info("AIService 종료 중...");
        globalCancellationFlag.set(true);
        log.info("AIService 종료 완료");
    }
}
