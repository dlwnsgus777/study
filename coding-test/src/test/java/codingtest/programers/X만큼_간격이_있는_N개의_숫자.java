package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class X만큼_간격이_있는_N개의_숫자 {

    @ParameterizedTest
    @CsvSource(value = {
        "2;5;2,4,6,8,10",
        "4;3;4,8,12",
        "-4;2;-4,-8",
    }, delimiter = ';')
    void test01(int x, int n, String expected) {
        long[] expectedArray = Arrays.stream(expected.split(","))
                                 .mapToLong(Long::parseLong)
                                 .toArray();


        assertThat(solution(x, n)).isEqualTo(expectedArray);
    }

    private long[] solution(int x, int n) {
        long[] result = new long[n];
        result[0] = x;

        for(int i = 1; i < n; i++) {
            result[i] = result[i - 1] + x;
        }

        return result;
    }
}
