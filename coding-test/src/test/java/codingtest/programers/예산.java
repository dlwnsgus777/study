package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 예산 {

    @ParameterizedTest
    @CsvSource(value = {
        "1,3,2,5,4;9;3",
        "2,2,3,3;10;4",
        "2,2,3,3;3;1",
    }, delimiter = ';')
    void test01(String d, int budget, int expected) {
        int[] dArr = Arrays.stream(d.split(","))
                             .mapToInt(Integer::parseInt)
                             .toArray();

        assertThat(solution(dArr, budget)).isEqualTo(expected);
    }

    private int solution(int[] d, int budget) {
        Arrays.sort(d);
        int answer = 0;
        for (int i = 0; i < d.length; i++) {
            if (d[i] <= budget) {
                answer++;
            }
            budget -= d[i];
        }

        return answer;
    }
}
