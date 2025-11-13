package com.codingtest.programmers.고득점키트.스택큐;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 주식가격Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] prices, int[] answer) {
        주식가격 solution = new 주식가격();
        int[] sut = solution.solution(prices);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(new int[] {1, 2, 3, 2, 3}, new int[] {4, 3, 1, 1, 0}),
            Arguments.of(new int[] {1, 2, 3, 4, 5}, new int[] {4, 3, 2, 1, 0}),
            Arguments.of(new int[] {5, 4, 3, 2, 1}, new int[] {1, 1, 1, 1, 0}));
    }
}