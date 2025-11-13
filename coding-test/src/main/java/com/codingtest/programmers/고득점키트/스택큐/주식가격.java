package com.codingtest.programmers.고득점키트.스택큐;

public class 주식가격 {

    public int[] solution(int[] prices) {
        int[] answer = new int[prices.length];
        int index = 0;
        while (index < prices.length - 1) {
            int count = 0;
            int target = prices[index];
            for (int i = index + 1; i < prices.length; i++) {
                int q = prices[i];

                count++;
                if (target > q) {
                    break;
                }
            }

            answer[index] = count;
            index++;
        }

        answer[prices.length - 1] = 0;
        return answer;
    }
}
