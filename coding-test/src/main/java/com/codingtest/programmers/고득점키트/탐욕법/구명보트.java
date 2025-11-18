package com.codingtest.programmers.고득점키트.탐욕법;

import java.util.Arrays;
import java.util.Stack;

public class 구명보트 {
    public int solution(int[] people, int limit) {
        Arrays.sort(people);

        int i = 0;                      // 가장 가벼운 사람 인덱스
        int j = people.length - 1;      // 가장 무거운 사람 인덱스
        int count = 0;                  // 보트 개수

        while (i <= j) {
            // 가장 가벼운(i) + 가장 무거운(j)이 같이 탈 수 있으면 같이 태움
            if (people[i] + people[j] <= limit) {
                i++;    // 가벼운 사람 탑승
            }
            // 무거운 사람은 항상 태움 (혼자 타든 같이 타든)
            j--;
            count++;    // 보트 한 대 사용
        }

        return count;
    }
}
