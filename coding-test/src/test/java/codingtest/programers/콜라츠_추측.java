package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 콜라츠_추측 {
    @ParameterizedTest
    @CsvSource(value = {
        "1;0",
        "6;8",
        "626331;-1",
    }, delimiter = ';')
    void test01(int input, int expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private int solution(int input) {
        if (input == 1) {
            return 0;
        }
        int answer = 0;
        long value = input;

        while (answer <= 500) {
            if (value % 2 == 0) {
                value = value / 2;
            } else {
                value = value * 3 + 1;
            }

            if (value == 1) {
                answer++;
                break;
            }

            answer++;
        }

        if (value == 1) {
            return answer;
        } else {
            return -1;
        }
    }
}
