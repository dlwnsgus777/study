package com.codingtest.programmers.고득점키트.완전탐색;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class 모의고사 {
    private static final int[] ONE = new int[]{1,2,3,4,5};
    private static final int[] TWO = new int[]{2,1,2,3,2,4,2,5};
    private static final int[] THREE = new int[]{3,3,1,1,2,2,4,4,5,5};

    public int[] solution(int[] answers) {
        int[] user = {0, 0, 0};
        int max = 0;
        for (int i = 0; i < answers.length; i++) {
            int a = answers[i];
            int oneA = ONE[i % ONE.length];
            int twoA = TWO[i % TWO.length];
            int threeA = THREE[i % THREE.length];

            if (a == oneA) {
                user[0]++;
                if (max < user[0]) {
                    max = user[0];
                }
            }

            if (a == twoA) {
                user[1]++;
                if (max < user[1]) {
                    max = user[1];
                }
            }

            if (a == threeA) {
                user[2]++;
                if (max < user[2]) {
                    max = user[2];
                }
            }
        }

        List<Integer> anw = new ArrayList<>();
        for (int i = 0; i < user.length; i++) {
            if (user[i] != 0 && user[i] == max) {
                anw.add(i + 1);
            }
        }
        anw.sort(Integer::compareTo);
        return anw.stream().mapToInt(Integer::intValue).toArray();
    }
}
