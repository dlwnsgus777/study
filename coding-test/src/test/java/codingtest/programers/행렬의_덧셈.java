package codingtest.programers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class 행렬의_덧셈 {

    @ParameterizedTest
    @MethodSource("provideMatrixData")
    void test01(int[][] arr1, int[][] arr2, int[][] expected) {
        assertThat(solution(arr1, arr2)).isDeepEqualTo(expected);
    }

    private int[][] solution(int[][] arr1, int[][] arr2) {
        int[][] answer = new int[arr1.length][arr1[0].length];

        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[i].length; j++) {
                answer[i][j] = arr1[i][j] + arr2[i][j];
            }
        }

        return answer;
    }

    static Stream<Arguments> provideMatrixData() {
        return Stream.of(
            Arguments.of(
                new int[][]{{1, 2}, {2, 3}},
                new int[][]{{3, 4}, {5, 6}},
                new int[][]{{4, 6}, {7, 9}}
                        ),
            Arguments.of(
                new int[][]{{1}, {2}},
                new int[][]{{3}, {4}},
                new int[][]{{4}, {6}}
                        )
                        );
    }
}
