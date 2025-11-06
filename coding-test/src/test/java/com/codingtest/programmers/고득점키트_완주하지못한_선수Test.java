package com.codingtest.programmers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 고득점키트_완주하지못한_선수Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(String[] participant, String[] completion, String answer) {
        고득점키트_완주하지못한_선수 solution = new 고득점키트_완주하지못한_선수();
        String sut = solution.solution(participant, completion);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new String[] {"leo", "kiki", "eden"},
                new String[] {"eden", "kiki"},
                "leo"
                        ),
            Arguments.of(
                new String[] {"mislav", "stanko", "mislav", "ana"},
                new String[] {"stanko", "ana", "mislav"},
                "mislav"
                        )
            );
    }
}
