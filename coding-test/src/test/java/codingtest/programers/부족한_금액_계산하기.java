package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class 부족한_금액_계산하기 {

    @ParameterizedTest
    @CsvSource(value = {
        "3;20;4;10",
        "100;7;4;993",
    }, delimiter = ';')
    void test01(int price, int money, int count, int expected) {
        assertThat(solution(price, money, count)).isEqualTo(expected);
    }

    private long solution(int price, int money, int count) {
        long totalMoney = 0;
        for (int i = 1; i <= count; i++) {
            totalMoney = totalMoney + ((long) price * i);
        }

        if (totalMoney <= money) {
            return 0;
        }

        return totalMoney - money;
    }
}
