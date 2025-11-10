package com.codingtest.programmers.고득점키트.스택큐;

import java.util.Stack;

public class 같은_숫자는_싫어 {
    public int[] solution(int []arr) {
        Stack<Integer> stack = new Stack<>();
        for (int a: arr) {
            if (stack.isEmpty() || stack.peek() != a) {
                stack.push(a);
            }
        }

        return stack.stream().mapToInt(Integer::intValue).toArray();
    }
}
