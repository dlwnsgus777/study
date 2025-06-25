package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 나누어_떨어지는_숫자_배열 {
    @ParameterizedTest
    @CsvSource(value = {
        "5,9,7,10;5;5,10",
        "2,36,1,3;1;1,2,3,36",
    }, delimiter = ';')
    void test01(String input, int divisor, String expected) {
        int[] inputInt = Arrays.stream(input.split(","))
                               .mapToInt(Integer::parseInt)
                               .toArray();

        int[] expectedArray = Arrays.stream(expected.split(","))
                               .mapToInt(Integer::parseInt)
                               .toArray();

        assertThat(solution(inputInt, divisor)).isEqualTo(expectedArray);
    }

    private int[] solution(int[] input, int divisor) {
        ArrayList<Integer> answer = new ArrayList<>();
        for (int j : input) {
            if ((j % divisor) == 0) {
                answer.add(j);
            }
        }

        if (answer.isEmpty()) {
            return new int[]{-1};
        }

        return answer.stream().sorted()
                      .mapToInt(Integer::intValue)
                      .toArray();
    }
}
