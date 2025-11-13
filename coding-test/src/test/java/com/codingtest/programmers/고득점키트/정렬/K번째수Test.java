package com.codingtest.programmers.고득점키트.정렬;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class K번째수Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] array, int[][] commands, int[] answer) {
        K번째수 solution = new K번째수();
        int[] sut = solution.solution(array, commands);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new int[] {1, 5, 2, 6, 3, 7, 4},
                new int[][] {
                    {2,5,3},
                    {4,4,1},
                    {1,7,3}
                },
                new int[] {5,6,3})
                        );
    }
}