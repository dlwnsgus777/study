package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 크기가_작은_부분_문자열 {

    @ParameterizedTest
    @CsvSource(value = {
        "3141592;271;2",
        "500220839878;7;8",
        "10203;15;3",
        "1;1;1",
        "10;1;2",
    }, delimiter = ';')
    void test01(String t, String p, int expected) {
        assertThat(solution(t, p)).isEqualTo(expected);
    }

    private int solution(String t, String p) {
        int length = p.length();
        int answer = 0;

        int start = 0;
        while(start + length <= t.length()) {
            String test = t.substring(start, length + start);
            if (Long.parseLong(test) <= Long.parseLong(p)) {
                answer++;
            }
            start++;
        }

        return answer;
    }
}
