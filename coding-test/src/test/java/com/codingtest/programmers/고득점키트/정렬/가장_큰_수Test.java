package com.codingtest.programmers.고득점키트.정렬;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.codingtest.programmers.고득점키트.해시.고득점키트_완주하지못한_선수;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 가장_큰_수Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] participant, String answer) {
        가장_큰_수 solution = new 가장_큰_수();
        String sut = solution.solution(participant);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new int[] {6,10,2},
                "6210"
                        ),
            Arguments.of(
                new int[] {3, 30, 34, 5, 9},
                "9534330"
                        )
                        );
    }
}