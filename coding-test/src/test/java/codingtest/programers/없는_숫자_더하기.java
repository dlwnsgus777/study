package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 없는_숫자_더하기 {
    @ParameterizedTest
    @CsvSource(value = {
        "1,2,3,4,6,7,8,0;14",
    }, delimiter = ';')
    void test01(String input, int expected) {
        int[] inputInt = Arrays.stream(input.split(","))
                               .mapToInt(Integer::parseInt)
                               .toArray();

        assertThat(solution(inputInt)).isEqualTo(expected);
    }

    private long solution(int[] input) {
        int answer = 0;
        List<Integer> array = Arrays.stream(input)
                                    .boxed()
                                    .collect(Collectors.toList());
        for (int i = 0; i <= 9; i++) {
            if (!array.contains(i)) {
                answer += i;
            }
        }

        return answer;
    }
}
