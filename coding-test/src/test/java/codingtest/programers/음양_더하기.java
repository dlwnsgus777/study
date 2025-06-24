package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 음양_더하기 {
    @ParameterizedTest
    @CsvSource(value = {
        "4,7,12;true,false,true;9",
        "1,2,3;false,false,true;0",
    }, delimiter = ';')
    void test01(String input, String inputSigns, int expected) {
        int[] inputInt = Arrays.stream(input.split(","))
                               .mapToInt(Integer::parseInt)
                               .toArray();

        Boolean[] signs = Arrays.stream(inputSigns.split(","))
                                .map(Boolean::parseBoolean)
                                .toArray(Boolean[]::new);

        boolean[] param = new boolean[signs.length];
        for (int i = 0; i < signs.length; i++) {
            param[i] = signs[i];
        }

        assertThat(solution(inputInt, param)).isEqualTo(expected);
    }

    private long solution(int[] absolutes, boolean[] signs) {
        long sum = 0;

        for (int i = 0; i < absolutes.length; i++) {
            sum += signs[i] ? absolutes[i] : -absolutes[i];

        }

        return sum;
    }
}
