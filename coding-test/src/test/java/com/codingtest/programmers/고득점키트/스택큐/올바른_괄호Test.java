package com.codingtest.programmers.고득점키트.스택큐;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 올바른_괄호Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(String s, boolean answer) {
        올바른_괄호 solution = new 올바른_괄호();
        boolean sut = solution.solution(s);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                "()()",
                true
                        ),
            Arguments.of(
                "(())()",
                true
                        ),
            Arguments.of(
                "(()(",
                false
                        )
            );
    }
}