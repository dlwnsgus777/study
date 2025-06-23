package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 나머지가_1이되는_수 {

    @ParameterizedTest
    @CsvSource(value = {
        "10;3",
        "12;11",
    }, delimiter = ';')
    void test01(int input, int expected) {

        assertThat(solution(input)).isEqualTo(expected);
    }

    private int solution(int input) {
        // 1부터 시작하여 input을 나누었을 때 나머지가 1이 되는 가장 작은 수를 찾습니다.
        for (int i = 2; i < input; i++) {
            if (input % i == 1) {
                return i;
            }
        }
       return 0;
    }
}
