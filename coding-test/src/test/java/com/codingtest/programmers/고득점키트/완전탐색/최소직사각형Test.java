package com.codingtest.programmers.고득점키트.완전탐색;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 최소직사각형Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[][] sizes, int answer) {
        최소직사각형 solution = new 최소직사각형();
        int sut = solution.solution(sizes);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new int[][] { {60, 50}, {30, 70}, {60, 30}, {80, 40} },
                4000
                        ),
            Arguments.of(
                new int[][] { {10, 7}, {12, 3}, {8, 15}, {14, 7}, {5, 15} },
                120
                        ),
            Arguments.of(
                new int[][] { {14, 4}, {19, 6}, {6, 16}, {18, 7}, {7, 11} },
                133
                        )
                        );
    }
}