package com.codingtest.programmers.고득점키트.스택큐;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 같은_숫자는_싫어Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int [] arr, int [] answer) {
        같은_숫자는_싫어 solution = new 같은_숫자는_싫어();
        int[] sut = solution.solution(arr);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new int[] {1,1,3,3,0,1,1,},
                new int[] {1,3,0,1}),
            Arguments.of(
                new int[] {4,4,4,3,3},
                new int[] {4,3})
        );
    }

}