package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 문자열_내림차순으로_배치하기 {

    @ParameterizedTest
    @CsvSource(value = {
        "Zbcdefg;gfedcbZ",
    }, delimiter = ';')
    void test01(String input, String expected) {
        assertThat(solution(input)).isEqualTo(expected);
    }

    private String solution(String n) {
        return Arrays.stream(n.split(""))
                     .sorted(Comparator.reverseOrder())
                     .collect(Collectors.joining());
    }
}
