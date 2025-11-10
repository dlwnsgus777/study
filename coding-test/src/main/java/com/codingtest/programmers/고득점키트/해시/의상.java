package com.codingtest.programmers.고득점키트.해시;

import java.util.HashMap;
import java.util.Map;

public class 의상 {
    public int solution(String[][] clothes) {
        int answer = 0;
        Map<String, Integer> cmap = new HashMap<>();
        for (String[] cloth : clothes) {
            cmap.put(cloth[1], cmap.getOrDefault(cloth[1], 0) + 1);
        }

        answer = cmap.values().stream().reduce(0, (a, b) -> a + b);
        answer += cmap.size();
        return answer;
    }
}
