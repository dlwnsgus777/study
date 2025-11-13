package com.codingtest.programmers.고득점키트.정렬;

import java.util.Arrays;

public class 가장_큰_수 {
    public String solution(int[] numbers) {
        // 1. 숫자를 문자열로 변환
        String[] arr = Arrays.stream(numbers)
                             .mapToObj(String::valueOf)
                             .toArray(String[]::new);

        // 2. 정렬 기준: (b + a)와 (a + b)를 비교해서 더 큰 쪽이 앞으로 오게
        Arrays.sort(arr, (a, b) -> (b + a).compareTo(a + b));

        // 3. 가장 큰 수가 "0"으로 시작하면 전체가 0인 경우
        if (arr[0].equals("0")) {
            return "0";
        }

        // 4. 이어 붙여서 반환
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s);
        }
        return sb.toString();
    }
}
