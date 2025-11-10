package com.codingtest.programmers.고득점키트.스택큐;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class 기능개발 {
    public int[] solution(int[] progresses, int[] speeds) {
        Queue<Integer> queue = new ArrayDeque<>();
        for (int p : progresses) queue.add(p);

        List<Integer> answer = new ArrayList<>();
        int q = 0;          // 경과일
        int index = 0;      // speeds 인덱스 (queue와 함께 전진)
        int count = 0;      // 현재 배포 묶음에 들어가는 기능 수

        while (!queue.isEmpty()) {
            int pro = queue.peek();
            int speed = speeds[index];
            int tte = pro + speed * q; // 현재 시점 q에서의 진도

            if (tte >= 100) {
                // 현재 시점에 배포 가능 → 같은 배포 묶음에 포함
                count++;
                index++;
                queue.poll(); // 다음 작업으로
            } else {
                // 현재 시점에 배포 불가 → 지금까지의 묶음을 확정(있다면)
                if (count > 0) {
                    answer.add(count);
                    count = 0;
                }
                // 선두 작업이 완성되도록 q를 '올림'으로 점프
                int remain = 100 - tte;
                int jump = (remain + speed - 1) / speed; // 올림
                q += jump;
            }
        }

        // 마지막 묶음 반영
        if (count > 0) answer.add(count);

        return answer.stream().mapToInt(Integer::intValue).toArray();
    }
}
