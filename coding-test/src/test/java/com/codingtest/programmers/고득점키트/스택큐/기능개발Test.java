package com.codingtest.programmers.고득점키트.스택큐;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.codingtest.programmers.고득점키트.해시.고득점키트_완주하지못한_선수;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 기능개발Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] progresses, int[] speeds, int[] answer) {
        기능개발 solution = new 기능개발();
        int[] sut = solution.solution(progresses, speeds);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new int[] {93, 30, 55},
                new int[] {1, 30, 5},
                new int[] {2, 1}
                        ),
            Arguments.of(
                new int[] {95, 90, 99, 99, 80, 99},
                new int[] {1, 1, 1, 1, 1, 1},
                new int[] {1, 3, 2}));
    }
}