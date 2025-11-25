package com.codingtest.programmers.고득점키트.탐욕법;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Collectors;

public class 큰_수_만들기 {
    public String solution(String number, int k) {
        int[] narr = Arrays.stream(number.split(""))
                           .mapToInt(Integer::valueOf)
                           .toArray();
        Stack<Integer> stack = new Stack<>();
        stack.add(narr[0]);
        for (int i = 1; i < narr.length; i++) {
            int num = narr[i];
            while(!stack.isEmpty() && k > 0 && stack.peek() < num) {
                stack.pop();
                k--;
            }

            stack.add(num);
        }

        while(k > 0) {
            stack.pop();
            k--;
        }

        return stack.stream().map(Object::toString).collect(Collectors.joining());
    }
}
