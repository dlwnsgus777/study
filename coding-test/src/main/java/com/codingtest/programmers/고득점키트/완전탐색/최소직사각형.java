package com.codingtest.programmers.고득점키트.완전탐색;

public class 최소직사각형 {
    public int solution(int[][] sizes) {
        int w = 0;
        int h = 0;
        for (int[] size : sizes) {
            int ew = size[0];
            int eh = size[1];

            int rw = ew;
            int rh = eh;

            if (ew < eh) {
                rw = eh;
            }

            if (w < rw) {
                w = rw;
            }

            if (eh > ew) {
                rh = ew;
            }

            if (h < rh) {
                h = rh;
            }
        }

        return h * w;
    }

}
