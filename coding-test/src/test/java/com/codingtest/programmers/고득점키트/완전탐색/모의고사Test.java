package com.codingtest.programmers.고득점키트.완전탐색;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 모의고사Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] sizes, int[] answer) {
        모의고사 solution = new 모의고사();
        int[] sut = solution.solution(sizes);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(new int[] {1, 2, 3, 4, 5}, new int[] {1}),
            Arguments.of(new int[] {1,3,2,4,2}, new int[] {1,2,3}),
            Arguments.of(new int[] {1}, new int[] {1}),
            Arguments.of(new int[] {1,2,2}, new int[] {1}));
    }
}