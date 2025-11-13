package com.codingtest.programmers.고득점키트.정렬;

import java.util.Arrays;

public class H_Index {
    public int solution(int[] citations) {
        if (citations.length == 1) {
            return citations[0] == 0 ? 0 : 1;
        }
        int answer = 0;
        Arrays.sort(citations);

        for (int i = 0; i < citations.length; i++) {
            int in = citations[i];
            int value = citations.length - i;
            if (in >= value) {
                if (answer < value) {
                    answer = value;
                }
            }
        }

        return answer;
    }
}
