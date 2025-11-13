package com.codingtest.programmers.고득점키트.힙;

import java.util.PriorityQueue;

public class 더_맵게 {

    public int solution(int[] scoville, int K) {
        PriorityQueue<Integer> qp = new PriorityQueue<>();
        int answer = 0;
        if (K == 0) {
            return answer;
        }

        for (int scovi: scoville) {
            if (scovi == 0) {
                return -1;
            }
            qp.offer(scovi);
        }

        while(true) {
            Integer first = qp.poll();
            if (first >= K) {
                break;
            } else {
                Integer second = qp.poll();
                if (second == null) {
                    return -1;
                }
                Integer value = first + (second * 2);
                qp.offer(value);
                answer++;
            }
        }

        return answer;
    }
}
