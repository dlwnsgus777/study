package com.codingtest.programmers.고득점키트.스택큐;

import java.util.LinkedList;
import java.util.Queue;

public class 다리를_지나는_트럭 {

    public int solution(int bridge_length, int weight, int[] truck_weights) {
        Queue<Integer> bridge = new LinkedList<>();
        int time = 0;
        int currentWeight = 0;
        int idx = 0; // 다음에 올라갈 트럭 인덱스

        // 처음엔 다리가 비어 있으므로 0으로 채워둔다
        for (int i = 0; i < bridge_length; i++) {
            bridge.add(0);
        }

        while (idx < truck_weights.length) {
            time++;

            // 1. 한 칸 전진: 맨 앞 트럭(or 0)이 다리에서 내려감
            currentWeight -= bridge.poll();

            int nextTruck = truck_weights[idx];

            // 2. 다음 트럭을 올릴 수 있으면 올림
            if (currentWeight + nextTruck <= weight) {
                bridge.add(nextTruck);
                currentWeight += nextTruck;
                idx++;
            } else {
                // 못 올리면 0(빈 칸)만 추가
                bridge.add(0);
            }
        }

        // 마지막 트럭이 다리를 완전히 빠져나가는 데 걸리는 추가 시간
        return time + bridge_length;
    }
}
