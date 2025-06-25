package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 제일_작은_수_제거하기 {
    @ParameterizedTest
    @CsvSource(value = {
        "4,3,2,1;4,3,2",
        "10;-1",
    }, delimiter = ';')
    void test01(String input, String expected) {
        int[] arrayInput = Arrays.stream(input.split(","))
                                 .mapToInt(Integer::parseInt)
                                 .toArray();

        int[] arrayExpected = Arrays.stream(expected.split(","))
                                 .mapToInt(Integer::parseInt)
                                 .toArray();
        assertThat(solution(arrayInput)).isEqualTo(arrayExpected);
    }


    private int[] solution(int[] input) {
        if (input.length == 1) {
            return new int[] {-1};
        }
        int min = Arrays.stream(input)
                        .filter(j -> j <= 999999)
                        .min()
                        .orElse(999999);


        return Arrays.stream(input)
                     .filter(it -> it != min)
                     .toArray();
    }

}
