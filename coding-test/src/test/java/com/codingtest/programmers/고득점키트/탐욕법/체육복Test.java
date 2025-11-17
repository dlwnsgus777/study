package com.codingtest.programmers.고득점키트.탐욕법;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.codingtest.programmers.고득점키트.해시.의상;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 체육복Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int n, int[] lost, int[] reserve, int answer) {
        체육복 solution = new 체육복();
        int sut = solution.solution(n, lost, reserve);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(3, new int[]{1,2}, new int[]{1,3}, 3),
            Arguments.of(5, new int[]{2,4}, new int[]{1,3,5}, 5),
            Arguments.of(5, new int[]{2,4}, new int[]{3}, 4),
            Arguments.of(3, new int[]{3}, new int[]{1}, 2),
            Arguments.of(5, new int[]{1,3,5}, new int[]{1,2,4}, 5)
                        );
    }
}