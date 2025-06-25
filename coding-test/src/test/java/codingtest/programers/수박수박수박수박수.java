package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 수박수박수박수박수 {
    @ParameterizedTest
    @CsvSource(value = {
        "3;수박수",
        "4;수박수박",
        "5;수박수박수",
    }, delimiter = ';')
    void test01(int n, String expected) {
        assertThat(solution(n)).isEqualTo(expected);
    }

    private String solution(int n) {
        StringBuilder answer = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (i % 2 == 0) {
                answer.append("박");
            } else {
                answer.append("수");
            }
        }

        return answer.toString();
    }
}
