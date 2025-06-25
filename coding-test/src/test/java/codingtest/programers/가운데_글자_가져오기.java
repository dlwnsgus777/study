package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 가운데_글자_가져오기 {
    @ParameterizedTest
    @CsvSource(value = {
        "abcde;c",
        "qwer;we",
    }, delimiter = ';')
    void test01(String input, String expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private String solution(String input) {
        if (input.length() % 2 == 0) {
            return input.substring(input.length() / 2 - 1, 3);
        } else {
            return input.substring(input.length() / 2, input.length() / 2 + 1);
        }

    }
}
