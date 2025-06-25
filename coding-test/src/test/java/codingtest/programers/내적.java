package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 내적 {
    @ParameterizedTest
    @CsvSource(value = {
        "1,2,3,4;-3,-1,0,2;3",
        "-1,0,1;1,0,-1;-2",
    }, delimiter = ';')
    void test01(String inputA, String inputB, int expected) {
        int[] aArr = Arrays.stream(inputA.split(","))
                           .mapToInt(Integer::parseInt)
                           .toArray();

        int[] bArr = Arrays.stream(inputB.split(","))
                           .mapToInt(Integer::parseInt)
                           .toArray();

        assertThat(solution(aArr, bArr)).isEqualTo(expected);
    }

    private int solution(int[] a, int[] b) {
        int answer = 0;
        for (int i = 0; i < a.length; i++) {
            answer += a[i] * b[i];
        }

        return answer;
    }
}
