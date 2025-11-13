package com.codingtest.programmers.고득점키트.힙;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.codingtest.programmers.고득점키트.해시.고득점키트_완주하지못한_선수;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 더_맵게Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] scoville, int K, int answer) {
        더_맵게 solution = new 더_맵게();
        int sut = solution.solution(scoville, K);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(new int[] {1, 2, 3, 9, 10, 12}, 7, 2),
            Arguments.of(new int[] {7, 7, 7, 7, 7, 5}, 7, 1),
            Arguments.of(new int[] {7, 1}, 8, 1),
            Arguments.of(new int[] {8}, 10, -1),
            Arguments.of(new int[] {8}, 8, 0),
            Arguments.of(new int[] {8, 7, 1, 1, 0}, 8, -1),
            Arguments.of(new int[] {8, 7, 1, 1, 0}, 0, 0),
            Arguments.of(new int[] {8, 1}, 11, 1),
            Arguments.of(new int[] {3, 1, 1, 2}, 3, 2),
            Arguments.of(new int[] {3, 1, 2}, 20, -1));
    }
}