package pluto.upik.shared.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
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

    @Value("${gemini.api.fallback-model:gemini-1.5-flash}")
    private String fallbackModel;

    @Autowired
    public ChatAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String askToDeepSeekAI(String question) {
        try {
            return callGemini(question, model);
        } catch (HttpStatusCodeException e) {
            // AI Studio 무료 티어 한도 초과(429) 등일 때 모델을 변경해 재시도
            if (shouldRetryWithFallback(e) && !model.equalsIgnoreCase(fallbackModel)) {
                log.warn("Gemini API 한도 문제 발생, 대체 모델로 재시도합니다. 원인: {} / 기본 모델: {}, 대체 모델: {}",
                        e.getStatusCode(), model, fallbackModel);
                try {
                    return callGemini(question, fallbackModel);
                } catch (Exception retryException) {
                    log.error("Gemini 대체 모델 호출 실패: {}", retryException.getMessage(), retryException);
                }
            }
            log.error("Gemini API 호출 실패 - 상태 코드: {}, 응답 본문: {}", e.getStatusCode(), safeBody(e), e);
            return "AI 서비스 통신 오류: " + e.getStatusCode();
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

    private String callGemini(String question, String targetModel) {
        String url = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                targetModel, apiKey
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

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

        log.debug("Gemini API 호출 시작 - 모델: {}, 요청 길이: {} 글자", targetModel, question.length());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        log.debug("Gemini API 호출 완료 - 상태 코드: {}, 모델: {}", response.getStatusCode(), targetModel);

        Map<String, Object> respBody = response.getBody();
        if (respBody != null && respBody.containsKey("candidates")) {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) respBody.get("candidates");
            if (!candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                if (content != null && content.containsKey("parts")) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                        String result = parts.get(0).get("text").toString();
                        log.debug("Gemini API 응답 성공 - 모델: {}, 응답 길이: {} 글자", targetModel, result.length());
                        return result;
                    }
                }
            }
        }

        log.warn("Gemini API 응답 형식이 예상과 다릅니다. 모델: {}, 응답: {}", targetModel, respBody);
        throw new IllegalStateException("Gemini API 응답을 파싱하지 못했습니다.");
    }

    private boolean shouldRetryWithFallback(HttpStatusCodeException e) {
        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return true;
        }
        String body = safeBody(e).toLowerCase();
        return body.contains("quota") || body.contains("resource_exhausted");
    }

    private String safeBody(HttpStatusCodeException e) {
        try {
            return e.getResponseBodyAsString();
        } catch (Exception ex) {
            return "";
        }
    }
}
