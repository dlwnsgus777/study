package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 문자열_내_p와_y의_개수 {
    @ParameterizedTest
    @CsvSource(value = {
        "pPoooyY;true",
        "Pyy;false",
        "Pyyppppp;false",
    }, delimiter = ';')
    void test01(String input, boolean expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private boolean solution(String input) {
        int tCnt = 0;
        int yCnt = 0;

        for (char c : input.toLowerCase().toCharArray()) {
            if (c == 'p') {
                tCnt++;
            }

            if (c == 'y') {
                yCnt++;
            }
        }

        return tCnt == yCnt;
    }
}
