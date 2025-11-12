package com.codingtest.programmers.고득점키트.스택큐;

import java.util.Stack;

public class 올바른_괄호 {
    boolean solution(String s) {
        String[] sArr = s.split("");
        Stack<String> stack = new Stack<>();
        boolean answer = true;
        if (sArr[0].equals(")")) {
            return false;
        }
        for (String a : sArr) {
            if(a.equals("(")) {
                stack.push(a);
            } else {
                if (stack.isEmpty()) {
                    answer = false;
                    break;
                }

                stack.pop();
            }
        }
        return answer && stack.isEmpty();
    }
}
