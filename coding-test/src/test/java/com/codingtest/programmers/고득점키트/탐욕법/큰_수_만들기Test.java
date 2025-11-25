package com.codingtest.programmers.고득점키트.탐욕법;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 큰_수_만들기Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(String number, int k, String answer) {
        큰_수_만들기 solution = new 큰_수_만들기();
        String sut = solution.solution(number, k);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
//            Arguments.of(
//                "1924",
//                2,
//                "94"
//                        ),
//            Arguments.of(
//                "111111",
//                2,
//                "1111"
//                        ),
//            Arguments.of(
//                "123456789",
//                3,
//                "456789"
//                        ),
//            Arguments.of(
//                "111119",
//                3,
//                "119"
//                        ),
            Arguments.of(
                "1212121212",
                5,
                "21212"
                        )
                        );
    }
}