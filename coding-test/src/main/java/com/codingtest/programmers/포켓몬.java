package com.codingtest.programmers;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Set;

public class 포켓몬 {
    public int solution(int[] nums) {
        int answer = 0;
        Set<Integer> set = Arrays.stream(nums).boxed().collect(toSet());
        int n = nums.length / 2;

        if (set.size() <= n) {
            answer = set.size();
        } else {
            answer = n;
        }

        return answer;
    }
}
