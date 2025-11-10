package com.codingtest.programmers;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingtest.programmers.고득점키트.해시.전화번호_목록;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 전화번호_목록Test {

    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(String[] participant, boolean answer) {
        전화번호_목록 solution = new 전화번호_목록();
        boolean sut = solution.solution(participant);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(
                new String[] {"119", "97674223", "1195524421"},
               false
                        ),
            Arguments.of(
                new String[] {"123", "456", "78945612312312312312"},
                false
                        )
                        );
    }
}