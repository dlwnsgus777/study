package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;

public class 최대공약수와_최소공배수 {

    @ParameterizedTest
    @CsvSource(value = {
        "3;12;3,12",
    }, delimiter = ';')
    void test01(int n, int m, String expected) {
        int[] expectedParts = Arrays.stream(expected.split(","))
                                    .mapToInt(Integer::parseInt)
                                    .toArray();
        assertThat(solution(n, m)).isEqualTo(expectedParts);
    }

    private int[] solution(int n, int m) {
        int a = n;
        int b = m;
        if (a < b) {
            int temp = a;
            a = b;
            b = temp;
        }

        while (b != 0) {
            int r = a % b;
            a = b;
            b = r;
        }

        int lcm = n * m / a;

        return new int[]{a, lcm};
    }
}
