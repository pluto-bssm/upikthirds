package pluto.upik.shared.translation.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 번역 서비스
 * 한국어와 영어 간의 번역 기능을 제공합니다.
 * Google Cloud Translation API를 사용합니다.
 */
@Service
@Slf4j
public class TranslationService {

    private final Translate translate;

    /**
     * Google Cloud Translation API 키를 사용하여 TranslationService를 초기화합니다.
     * 
     * API 키가 제공되면 해당 키로 Google Cloud Translation 클라이언트를 설정하고,
     * 키가 없으면 기본 인증(환경 변수 GOOGLE_APPLICATION_CREDENTIALS에 지정된 서비스 계정)을 사용합니다.
     *
     * @param apiKey Google Cloud Translation API 키(선택적)
     */
    public TranslationService(@Value("${google.cloud.translation.api-key:}") String apiKey) {
        // API 키가 제공된 경우 사용, 그렇지 않으면 기본 인증 사용
        if (apiKey != null && !apiKey.isEmpty()) {
            this.translate = TranslateOptions.newBuilder()
                    .setApiKey(apiKey)
                    .build()
                    .getService();
        } else {
            // 기본 인증 사용 (환경 변수 GOOGLE_APPLICATION_CREDENTIALS에 지정된 서비스 계정 키 파일 사용)
            this.translate = TranslateOptions.getDefaultInstance().getService();
        }
    }

    /**
     * 한국어 텍스트를 영어로 번역합니다.
     *
     * @param koreanText 번역할 한국어 텍스트
     * @return 번역된 영어 텍스트
     */
    public String translateKoreanToEnglish(String koreanText) {
        log.debug("한국어를 영어로 번역: {}", koreanText);
        try {
            return translate(koreanText, "ko", "en");
        } catch (Exception e) {
            log.error("한국어 번역 중 오류 발생", e);
            // 번역 실패 시 원본 텍스트 반환
            return koreanText;
        }
    }

    /**
     * 영어 텍스트를 한국어로 번역합니다.
     *
     * @param englishText 번역할 영어 텍스트
     * @return 번역된 한국어 텍스트
     */
    public String translateEnglishToKorean(String englishText) {
        log.debug("영어를 한국어로 번역: {}", englishText);
        try {
            return translate(englishText, "en", "ko");
        } catch (Exception e) {
            log.error("영어 번역 중 오류 발생", e);
            // 번역 실패 시 원본 텍스트 반환
            return englishText;
        }
    }

    /**
     * Google Cloud Translation API를 사용하여 텍스트를 지정한 언어에서 다른 언어로 번역합니다.
     *
     * @param text 번역할 원본 텍스트
     * @param sourceLang 원본 언어의 ISO 코드(예: "ko", "en")
     * @param targetLang 번역 대상 언어의 ISO 코드(예: "en", "ko")
     * @return 번역된 텍스트. 번역에 실패하면 원본 텍스트를 반환합니다.
     */
    private String translate(String text, String sourceLang, String targetLang) {
        try {
            Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.sourceLanguage(sourceLang),
                    Translate.TranslateOption.targetLanguage(targetLang)
            );

            String translatedText = translation.getTranslatedText();
            log.debug("번역 완료: {}", translatedText);
            return translatedText;
        } catch (Exception e) {
            log.error("번역 API 호출 중 오류 발생", e);
            return text; // 번역 실패 시 원본 텍스트 반환
        }
    }
}
