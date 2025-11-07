package com.codingtest.programmers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 의상Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(String[][] participant, int answer) {
        의상 solution = new 의상();
        int sut = solution.solution(participant);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new String[][] {
                    {"yellow_hat", "headgear"},
                    {"blue_sunglasses", "eyewear"},
                    {"green_turban", "headgear"}
                },
                5
                        ),
            Arguments.of(
                new String[][] {
                    {"crow_mask", "face"},
                    {"blue_sunglasses", "face"},
                    {"smoky_makeup", "face"}
                },
                3
                        )

                        );
    }
}