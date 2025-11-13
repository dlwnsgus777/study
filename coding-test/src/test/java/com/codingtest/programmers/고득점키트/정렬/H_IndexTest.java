package com.codingtest.programmers.고득점키트.정렬;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class H_IndexTest {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] participant, int answer) {
        H_Index solution = new H_Index();
        int sut = solution.solution(participant);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(new int[] {3,0,6,1,5}, 3),
            Arguments.of(new int[] {0,0,0,1,1}, 1),
            Arguments.of(new int[] {0,1,2,3,4}, 2),
            Arguments.of(new int[] {5}, 1),
            Arguments.of(new int[] {3, 2, 0, 0, 1}, 2),
            Arguments.of(new int[] {100, 100, 100}, 3)
            );
    }
}