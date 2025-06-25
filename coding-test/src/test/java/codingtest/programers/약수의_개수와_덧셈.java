package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 약수의_개수와_덧셈 {
    @ParameterizedTest
    @CsvSource(value = {
        "13;17;43",
        "24;27;52",
    }, delimiter = ';')
    void test01(int left, int right, int expected) {
        assertThat(solution(left, right)).isEqualTo(expected);
    }

    private int solution(int left, int right) {
        int answer = 0;
        for (int i = left; i <= right; i++) {
            int count = 0;
            for (int j = 1; j <= i ; j++) {
                if (i % j == 0) {
                    count++;
                }
            }
            if (count % 2 == 0) {
                answer += i;
            } else {
                answer -= i;
            }
        }

        return answer;
    }
}
