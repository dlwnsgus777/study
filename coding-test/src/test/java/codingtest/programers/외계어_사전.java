package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 외계어_사전 {

    @ParameterizedTest
    @CsvSource(value = {
        "p,o,s;sod,eocd,qixm,adio,soo;2",
        "z,d,x;def,dww,dzx,loveaw;1",
        "s,o,m,d;mos,dzx,smm,sunmmo,som;2"
    }, delimiter = ';')
    void test01(String spell, String dic, int expected) {
        String[] s = spell.split(",");
        String[] d = dic.split(",");
        assertThat(solution(s, d)).isEqualTo(expected);
    }

    private int solution(String[] spell, String[] dic) {
        int answer = 2;

        for (String string : dic) {
            int index = 0;
            for (String s : spell) {
                if (string.contains(s)) {
                    index++;
                }

                if (index == spell.length) {
                    answer = 1;
                }
            }
        }

        return answer;
    }

}
