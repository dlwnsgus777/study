package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 평균_구하기 {

    @ParameterizedTest
    @CsvSource(value = {
        "1,2,3,4;2.5",
        "5,5;5",
        "2,4,5,100;27.75",
    }, delimiter = ';')
    void test01(String input, double expected) {
        int[] inputArray = Arrays.stream(input.split(","))
                                 .mapToInt(Integer::parseInt)
                                 .toArray();


        assertThat(solution(inputArray)).isEqualTo(expected);
    }

    private double solution(int[] input) {
        return Arrays.stream(input)
                     .average()
                     .orElse(0.0);
    }
}
