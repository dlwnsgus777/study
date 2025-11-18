package com.codingtest.programmers.고득점키트.탐욕법;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.codingtest.programmers.고득점키트.해시.고득점키트_완주하지못한_선수;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 구명보트Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int[] people, int limit, int answer) {
        구명보트 solution = new 구명보트();
        int sut = solution.solution(people, limit);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new int[] {70,50,80,50},
                100,
                3
                        ),
            Arguments.of(
                new int[] {100,100,100},
                100,
                3
                        ),
            Arguments.of(
                new int[] {40, 60, 40, 60},
                100,
                2
                        )
                        );
}
}