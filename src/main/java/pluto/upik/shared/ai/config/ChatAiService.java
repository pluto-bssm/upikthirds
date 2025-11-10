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

    @Value("${gemini.api.model:gemini-2.0-flash-exp}")
    private String model;

    @Autowired
    public ChatAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String askToDeepSeekAI(String question) {
        // Gemini REST API 엔드포인트
        String url = String.format(
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
            model, apiKey
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini API 요청 형식
        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of(
                    "parts", List.of(
                        Map.of("text", question)
                    )
                )
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            log.debug("Gemini API 호출 시작 - 요청 길이: {} 글자", question.length());
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            log.debug("Gemini API 호출 완료 - 상태 코드: {}", response.getStatusCode());

            Map<String, Object> respBody = response.getBody();
            if (respBody != null && respBody.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) respBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    if (content != null && content.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                            String result = parts.get(0).get("text").toString();
                            log.debug("Gemini API 응답 성공 - 응답 길이: {} 글자", result.length());
                            return result;
                        }
                    }
                }
            }

            log.warn("Gemini API 응답 형식이 예상과 다릅니다: {}", respBody);
            return "API로부터 응답을 받지 못했습니다";
        } catch (Exception e) {
            log.error("Gemini API 호출 중 오류 발생: {}", e.getMessage(), e);
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