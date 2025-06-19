package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 자연수_배열_뒤집기 {

    @ParameterizedTest
    @CsvSource(value = {
        "12345;5,4,3,2,1",
        "8;8",
        "123;3,2,1",
        "100023;3,2,0,0,0,1"
    }, delimiter = ';')
    void test01(long input, String expected) {
        int[] expectedArray = Arrays.stream(expected.split(","))
                                    .mapToInt(Integer::parseInt)
                                    .toArray();

        assertThat(solution(input)).isEqualTo(expectedArray);
    }

    private int[] solution(long input) {
        String str = String.valueOf(input);
        int[] result = new int[str.length()];

        for (int i = 0; i < str.length(); i++) {
            result[i] = Character.getNumericValue(str.charAt(str.length() - 1 - i));
        }

        return result;
    }
}
