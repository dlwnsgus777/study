package com.codingtest.programmers.고득점키트.정렬;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class K번째수 {
    public int[] solution(int[] array, int[][] commands) {
        List<Integer> arr = new ArrayList<>();
        for (int a : array) {
            arr.add(a);
        }
        List<Integer> answer = new ArrayList<>();
        for (int[] a : commands) {
            int f = a[0] - 1;
            int s = a[1];
            List<Integer> arr1 = new ArrayList<>();
            if (s == a[0]) {
                Integer e = arr.get(f);
                arr1.add(e);
            } else {
                arr1 = new ArrayList<>(arr.subList(f, s));
            }
            arr1.sort(Integer::compareTo);
            int index = a[2];
            answer.add(arr1.get(index - 1));
        }

        return answer.stream().mapToInt(Integer::intValue).toArray();
    }
}
