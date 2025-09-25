package pluto.upik.shared.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ChatAiService {
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Autowired
    public ChatAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String askToDeepSeekAI(String question) {
        // 한국어 채팅 대화 API 엔드포인트
        String url = "https://genai-app-koreanchatconversation-1-1757577861794-612486206975.us-central1.run.app/chat?key="+apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 형식을 지정된 형식으로 변경
        Map<String, Object> body = Map.of(
                "prompt", question
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            log.debug("한국어 채팅 API 호출 시작 - 요청 길이: {} 글자", question.length());
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            log.debug("한국어 채팅 API 호출 완료 - 상태 코드: {}", response.getStatusCode());

            Map<String, Object> respBody = response.getBody();
            if (respBody != null && respBody.containsKey("response")) {
                String result = respBody.get("response").toString();
                log.debug("한국어 채팅 API 응답 성공 - 응답 길이: {} 글자", result.length());
                return result;
            }

            log.warn("한국어 채팅 API 응답 형식이 예상과 다릅니다: {}", respBody);
            return "API로부터 응답을 받지 못했습니다";
        } catch (Exception e) {
            log.error("한국어 채팅 API 호출 중 오류 발생: {}", e.getMessage(), e);
            return "AI 서비스 통신 오류: " + e.getMessage();
        }
    }

    public Flux<String> askToDeepSeekAiWithStream(String quest) {
        // 스트리밍 응답 처리
        // 단일 응답을 Flux로 변환하여 반환
        String response = askToDeepSeekAI(quest);
        return Flux.just(response);
    }
}