package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 짝수와홀수 {

    @ParameterizedTest
    @CsvSource(value = {
        "3;Odd",
        "4;Even",
        "5;Odd",
        "1000000;Even"
    }, delimiter = ';')
    void test01(int input, String expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private String solution(int input) {
        return input % 2 == 0 ? "Even" : "Odd";
    }
}
