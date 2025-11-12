package com.codingtest.programmers.고득점키트.스택큐;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 프로세스Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] priorities, int location, int answer) {
        프로세스 solution = new 프로세스();
        int sut = solution.solution(priorities, location);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new int[] {2, 1, 3, 2},
                    2,
                    1),
            Arguments.of(
                new int[] {1, 1, 9, 1, 1, 1},
                    0,
                    5));
    }

}