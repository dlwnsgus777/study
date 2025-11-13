package com.codingtest.programmers.고득점키트.스택큐;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class 다리를_지나는_트럭Test {
    @ParameterizedTest
    @MethodSource("provideTestData")
    void test01(int bridge_length, int weight, int[] truck_weights, int answer) {
        다리를_지나는_트럭 solution = new 다리를_지나는_트럭();
        int sut = solution.solution(bridge_length, weight, truck_weights);
        assertThat(sut).isEqualTo(answer);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(2, 10, new int[] {7,4,5,6}, 8),
            Arguments.of(100, 100, new int[] {10}, 101));
//            Arguments.of(100, 100, new int[] {10,10,10,10,10,10,10,10,10,10}, 110));
    }

}