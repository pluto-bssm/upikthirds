package pluto.upik.shared.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 욕설 필터링 서비스
 * 사용자 입력에서 부적절한 언어를 감지합니다.
 */
@Slf4j
@Service
public class BadWordFilterService {

    private final Set<String> badWords;
    private final Set<Pattern> badWordPatterns;

    public BadWordFilterService() {
        this.badWords = initializeBadWords();
        this.badWordPatterns = initializeBadWordPatterns();
        log.info("BadWordFilterService initialized with {} words and {} patterns",
                badWords.size(), badWordPatterns.size());
    }

    /**
     * 텍스트에 욕설이 포함되어 있는지 확인합니다.
     *
     * @param text 검사할 텍스트
     * @return 욕설이 포함되어 있으면 true, 아니면 false
     */
    public boolean containsBadWord(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        String normalizedText = normalizeText(text);

        // 직접 매칭 확인
        for (String badWord : badWords) {
            if (normalizedText.contains(badWord)) {
                log.warn("Bad word detected: {} in text: {}", badWord, text);
                return true;
            }
        }

        // 패턴 매칭 확인 (공백이 포함된 경우)
        String textWithoutSpaces = text.replaceAll("\\s+", "");
        for (Pattern pattern : badWordPatterns) {
            if (pattern.matcher(textWithoutSpaces).find()) {
                log.warn("Bad word pattern detected in text: {}", text);
                return true;
            }
        }

        return false;
    }

    /**
     * 여러 텍스트 필드를 한번에 검사합니다.
     *
     * @param texts 검사할 텍스트들
     * @return 하나라도 욕설이 포함되어 있으면 true
     */
    public boolean containsBadWord(String... texts) {
        return Arrays.stream(texts)
                .filter(text -> text != null && !text.trim().isEmpty())
                .anyMatch(this::containsBadWord);
    }

    /**
     * 리스트의 모든 항목을 검사합니다.
     *
     * @param textList 검사할 텍스트 리스트
     * @return 하나라도 욕설이 포함되어 있으면 true
     */
    public boolean containsBadWordInList(List<String> textList) {
        if (textList == null || textList.isEmpty()) {
            return false;
        }

        return textList.stream()
                .anyMatch(this::containsBadWord);
    }

    /**
     * 텍스트를 정규화합니다 (소문자 변환, 특수문자 제거 등).
     *
     * @param text 원본 텍스트
     * @return 정규화된 텍스트
     */
    private String normalizeText(String text) {
        return text.toLowerCase()
                .replaceAll("[^가-힣a-z0-9ㄱ-ㅎㅏ-ㅣ]", "");
    }

    /**
     * 욕설 단어 목록을 초기화합니다.
     *
     * @return 욕설 단어 Set
     */
    private Set<String> initializeBadWords() {
        Set<String> words = new HashSet<>();

        // 기본 욕설
        words.addAll(Arrays.asList(
            "씨발", "시발", "씨빨", "시빨", "씨팔", "시팔",
            "ㅅㅂ", "ㅆㅂ", "ㅆ발", "ㅅ발",
            "개새끼", "개세끼", "개색", "개색끼", "개색히",
            "개자식", "개좆", "개년",
            "병신", "뱅신", "병쉰", "병시인",
            "지랄", "지걸", "쥐랄",
            "좆", "좃", "졷", "좉",
            "꺼져", "꺼지", "꺼지라", "꺼져라",
            "닥쳐", "닥치", "닥치라", "닥쳐라",
            "엿먹", "엿이나먹", "엿을먹",
            "18새", "18놈", "18년",
            "ㅈ같", "ㅈ까", "존나",
            "미친", "미쳤", "미치",
            "애미", "애비", "에미", "에비",
            "새끼", "색히", "색끼",
            "년", "놈",
            "호로", "후로", "창녀", "걸레",
            "쓰레기", "쓰렉", "쓰래기",
            "죽여", "죽어", "디져", "뒤져",
            "ㅄ", "ㅂㅅ",
            "느금", "느그", "니애미", "니애비",
            "등신", "돈신",
            "또라이", "띠발", "띠벌", "띠바"
        ));

        // 변형 및 은어
        words.addAll(Arrays.asList(
            "ssibal", "sibal", "fuck", "shit", "bitch",
            "tlqkf", "qkqh", "wlsgkr", // 자판 치환
            "ㅂㅅㅇ", "ㅄㅅㅇ" // 초성
        ));

        return words;
    }

    /**
     * 욕설 패턴 목록을 초기화합니다.
     * 공백이나 특수문자가 들어간 변형된 욕설을 감지합니다.
     *
     * @return 욕설 패턴 Set
     */
    private Set<Pattern> initializeBadWordPatterns() {
        Set<Pattern> patterns = new HashSet<>();

        // 기본 욕설의 패턴 (문자 사이에 특수문자나 공백이 있는 경우)
        patterns.add(Pattern.compile("[씨시][\\s*_-]*[발빨팔]"));
        patterns.add(Pattern.compile("개[\\s*_-]*새[\\s*_-]*끼"));
        patterns.add(Pattern.compile("개[\\s*_-]*[자좆]"));
        patterns.add(Pattern.compile("병[\\s*_-]*신"));
        patterns.add(Pattern.compile("지[\\s*_-]*랄"));
        patterns.add(Pattern.compile("[좆좃졷좉][\\s*_-]*"));

        return patterns;
    }

    /**
     * 욕설 필터에 커스텀 단어를 추가합니다.
     *
     * @param words 추가할 욕설 단어들
     */
    public void addBadWords(String... words) {
        badWords.addAll(Arrays.asList(words));
        log.info("Added {} custom bad words to filter", words.length);
    }

    /**
     * 욕설 필터에서 단어를 제거합니다.
     *
     * @param words 제거할 단어들
     */
    public void removeBadWords(String... words) {
        Arrays.stream(words).forEach(badWords::remove);
        log.info("Removed {} words from bad word filter", words.length);
    }
}
