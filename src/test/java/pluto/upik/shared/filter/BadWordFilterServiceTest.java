package pluto.upik.shared.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BadWordFilterService 단위 테스트
 */
@DisplayName("욕설 필터링 서비스 테스트")
class BadWordFilterServiceTest {

    private BadWordFilterService badWordFilterService;

    @BeforeEach
    void setUp() {
        badWordFilterService = new BadWordFilterService();
    }

    @Test
    @DisplayName("기본 욕설 감지 테스트")
    void testBasicBadWords() {
        // Given
        String[] badTexts = {
                "씨발",
                "개새끼",
                "병신",
                "좆",
                "지랄",
                "꺼져"
        };

        // When & Then
        for (String text : badTexts) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 욕설로 감지되어야 합니다", text)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("정상적인 텍스트는 욕설로 감지되지 않음")
    void testCleanText() {
        // Given
        String[] cleanTexts = {
                "안녕하세요",
                "좋은 아침입니다",
                "감사합니다",
                "투표에 참여해주세요",
                "의견을 남겨주세요"
        };

        // When & Then
        for (String text : cleanTexts) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 욕설로 감지되지 않아야 합니다", text)
                    .isFalse();
        }
    }

    @Test
    @DisplayName("공백이 포함된 욕설 감지 테스트")
    void testBadWordsWithSpaces() {
        // Given
        String[] textsWithSpaces = {
                "씨 발",
                "개 새 끼",
                "병 신",
                "지 랄"
        };

        // When & Then
        for (String text : textsWithSpaces) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 공백이 있어도 욕설로 감지되어야 합니다", text)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("초성 욕설 감지 테스트")
    void testConsonantBadWords() {
        // Given
        String[] consonantBadWords = {
                "ㅅㅂ",
                "ㅆㅂ",
                "ㅄ",
                "ㅂㅅ"
        };

        // When & Then
        for (String text : consonantBadWords) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 초성 욕설로 감지되어야 합니다", text)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("영어 욕설 감지 테스트")
    void testEnglishBadWords() {
        // Given
        String[] englishBadWords = {
                "fuck",
                "shit",
                "bitch"
        };

        // When & Then
        for (String text : englishBadWords) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 영어 욕설로 감지되어야 합니다", text)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("문장 내 욕설 감지 테스트")
    void testBadWordsInSentence() {
        // Given
        String text = "이건 정말 씨발 짜증나네요";

        // When
        boolean result = badWordFilterService.containsBadWord(text);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("여러 텍스트 중 하나라도 욕설이 있으면 감지")
    void testMultipleTexts() {
        // Given
        String cleanText = "안녕하세요";
        String badText = "씨발";

        // When
        boolean result = badWordFilterService.containsBadWord(cleanText, badText);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("리스트에서 욕설 감지 테스트")
    void testBadWordsInList() {
        // Given
        List<String> textList = Arrays.asList(
                "좋은 선택지 1",
                "나쁜 선택지 씨발",
                "좋은 선택지 2"
        );

        // When
        boolean result = badWordFilterService.containsBadWordInList(textList);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("빈 문자열은 욕설이 아님")
    void testEmptyString() {
        // Given
        String emptyText = "";

        // When
        boolean result = badWordFilterService.containsBadWord(emptyText);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("null은 욕설이 아님")
    void testNullString() {
        // Given
        String nullText = null;

        // When
        boolean result = badWordFilterService.containsBadWord(nullText);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("빈 리스트는 욕설이 아님")
    void testEmptyList() {
        // Given
        List<String> emptyList = Arrays.asList();

        // When
        boolean result = badWordFilterService.containsBadWordInList(emptyList);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("null 리스트는 욕설이 아님")
    void testNullList() {
        // Given
        List<String> nullList = null;

        // When
        boolean result = badWordFilterService.containsBadWordInList(nullList);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("커스텀 욕설 추가 테스트")
    void testAddCustomBadWords() {
        // Given
        String customBadWord = "커스텀욕설";
        badWordFilterService.addBadWords(customBadWord);

        // When
        boolean result = badWordFilterService.containsBadWord(customBadWord);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("욕설 제거 테스트")
    void testRemoveBadWords() {
        // Given
        String customBadWord = "테스트욕설";
        badWordFilterService.addBadWords(customBadWord);

        // 추가 확인
        assertThat(badWordFilterService.containsBadWord(customBadWord)).isTrue();

        // When
        badWordFilterService.removeBadWords(customBadWord);
        boolean result = badWordFilterService.containsBadWord(customBadWord);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("대소문자 구분 없이 욕설 감지")
    void testCaseInsensitive() {
        // Given
        String[] variations = {
                "FUCK",
                "Fuck",
                "fuck",
                "FuCk"
        };

        // When & Then
        for (String text : variations) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 대소문자 구분 없이 욕설로 감지되어야 합니다", text)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("특수문자가 섞인 욕설 감지")
    void testBadWordsWithSpecialCharacters() {
        // Given
        String[] textsWithSpecialChars = {
                "씨*발",
                "개-새-끼",
                "병_신"
        };

        // When & Then
        for (String text : textsWithSpecialChars) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 특수문자가 있어도 욕설로 감지되어야 합니다", text)
                    .isTrue();
        }
    }

    @Test
    @DisplayName("변형된 욕설 감지 테스트")
    void testVariationBadWords() {
        // Given
        String[] variations = {
                "시발",
                "씨팔",
                "뱅신",
                "쥐랄",
                "띠발"
        };

        // When & Then
        for (String text : variations) {
            assertThat(badWordFilterService.containsBadWord(text))
                    .as("'%s'는 변형된 욕설로 감지되어야 합니다", text)
                    .isTrue();
        }
    }
}
