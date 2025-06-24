package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 정수_제곱근_판별 {
    @ParameterizedTest
    @CsvSource(value = {
        "121;144",
        "3;-1",
    }, delimiter = ';')
    void test01(long input, long expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private long solution(long input) {
        // 입력값의 제곱근을 구합니다.
        double sqrt = Math.sqrt(input);

        // 제곱근이 정수인지 확인합니다.
        if (sqrt == (int) sqrt) {
            // 정수라면 제곱근에 1을 더한 후 제곱하여 반환합니다.
            return (long) Math.pow(sqrt + 1, 2);
        } else {
            // 정수가 아니라면 -1을 반환합니다.
            return -1;
        }
    }
}
