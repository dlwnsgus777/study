package com.codingtest.beakjoon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class DFS와BFS {

    public static StringBuilder SB = new StringBuilder();
    public static int[][] arr;
    public static boolean[] visit;
    public static int n;
    public static int m;

    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String[] st = bf.readLine()
                        .split(" ");

        n = Integer.parseInt(st[0]);
        m = Integer.parseInt(st[1]);
        int start = Integer.parseInt(st[2]);

        arr = new int[n + 1][n + 1];
        visit = new boolean[n + 1];

        for (int i = 1; i < m + 1; i++) {
            String[] st2 = bf.readLine()
                            .split(" ");
            int a = Integer.parseInt(st2[0]);
            int b = Integer.parseInt(st2[1]);

            arr[a][b] = arr[b][a] = 1;
        }

        dfs(start);
        SB.append("\n");
        visit = new boolean[n + 1];

        bfs(start);
        System.out.println(SB.toString());

        bf.close();
    }

    public static void dfs(int start) {
        visit[start] = true;
        SB.append(start + " ");

        for (int i = 1; i < n + 1; i++) {
            if (arr[start][i] == 1 && !visit[i]) {
                dfs(i);
            }
        }
    }

    public static void bfs(int start) {
        Queue<Integer> q = new LinkedList<>();
        q.add(start);
        visit[start] = true;

        while (!q.isEmpty()) {
            start = q.poll();
            SB.append(start + " ");
            for (int i = 1; i < n + 1; i++) {
                if ( arr[start][i] == 1 && !visit[i]) {
                    q.add(i);
                    visit[i] = true;
                }

            }
        }
    }
}
