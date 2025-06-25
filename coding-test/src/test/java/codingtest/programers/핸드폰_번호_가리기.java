package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 핸드폰_번호_가리기 {
    @ParameterizedTest
    @CsvSource(value = {
        "01033334444;*******4444",
        "027778888;*****8888",
    }, delimiter = ';')
    void test01(String input, String expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private String solution(String input) {
        String[] arr = input.split("");
        for (int i = 0; i < arr.length - 4; i++) {
            arr[i] = "*";
        }

        return String.join("", arr);
    }
}
