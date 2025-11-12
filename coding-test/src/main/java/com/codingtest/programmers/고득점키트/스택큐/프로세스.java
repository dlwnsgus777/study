package com.codingtest.programmers.고득점키트.스택큐;

import java.util.LinkedList;
import java.util.Queue;

public class 프로세스 {

    public int solution(int[] priorities, int location) {
        Queue<Proc> queue = new LinkedList<>();
        for (int i = 0; i < priorities.length; i++) {
            queue.add(new Proc(priorities[i], i));
        }

        int count = 0;
        while(!queue.isEmpty()) {
            Proc proc = queue.poll();
            boolean canProcess = true;
            for (Proc p : queue) {
                if (p.lowPriority(proc)) {
                    canProcess = false;
                }
            }

            if (canProcess) {
                count++;
                if (proc.currentLocation(location)) {
                    break;
                }
            } else {
                queue.add(proc);

            }
        }
        return count;
    }

    private static class Proc {
        private final int priority;
        private final int location;

        Proc(int priority, int location) {
            this.priority = priority;
            this.location = location;
        }

        public boolean lowPriority(Proc proc) {
            return this.priority > proc.priority;
        }

        public boolean currentLocation(int location) {
            return this.location == location;
        }
    }
}
