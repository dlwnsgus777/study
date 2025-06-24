package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 하샤드_수 {
    @ParameterizedTest
    @CsvSource(value = {
        "10;true",
        "12;true",
        "11;false",
    }, delimiter = ';')
    void test01(int input, boolean expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private boolean solution(int input) {
        // 입력값을 문자열로 변환하여 각 자리수를 더합니다.
        int sumOfDigits = String.valueOf(input)
                                .chars()
                                .map(Character::getNumericValue)
                                .sum();

        // 입력값이 자리수의 합으로 나누어 떨어지는지 확인합니다.
        return input % sumOfDigits == 0;
    }
}
