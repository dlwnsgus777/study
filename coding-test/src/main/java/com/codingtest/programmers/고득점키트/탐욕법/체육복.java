package com.codingtest.programmers.고득점키트.탐욕법;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class 체육복 {
    public int solution(int n, int[] lost, int[] reserve) {
        List<Student> st = new ArrayList<>();
        for (int i : lost) {
            st.add(new Student(0, i));
        }

        for (int i : reserve) {
            Optional<Student> found = st.stream()
                                        .filter(it -> it.getNumber() == i)
                                        .findAny();
            if (found.isEmpty()) {
                st.add(new Student(2, i));
            } else {
                Student std = found.get();
                std.increase();
            }

        }

        st.sort((e1, e2) -> {
            if (e1.getNumber() > e2.getNumber()) {
                return 1;
            } else if (e2.getNumber() > e1.getNumber()) {
                return -1;
            }
            return 0;
        });

        List<Student> ans = new ArrayList<>();
        for (int i = 0; i < st.size(); i++) {
            Student std = st.get(i);

            if (std.getCount() < 1) {
                if (i == 0) {
                    Student std2 = st.get(i + 1);
                    if (std2.getCount() > 1 && std2.getNumber() - 1 == std.getNumber()) {
                        std.increase();
                        std2.decrease();
                    }
                }

                if (i > 0) {
                    Student std2 = st.get(i - 1);
                    if (std2.getCount() > 1 && std2.getNumber() + 1 == std.getNumber()) {
                        std.increase();
                        std2.decrease();
                    } else if(i + 1 < st.size()) {
                        std2 = st.get(i + 1);
                        if (std2.getCount() > 1 && std2.getNumber() - 1 == std.getNumber()) {
                            std.increase();
                            std2.decrease();
                        }
                    }
                }

            }
            ans.add(std);
        }

        int count = (int) ans.stream()
                             .filter(it -> it.getCount() <= 0)
                             .count();

        return n - count;
    }

    public static class Student {
        private int count;
        private int number;

        public Student(int count, int number) {
            this.count = count;
            this.number = number;
        }

        public int getCount() {
            return count;
        }

        public int getNumber() {
            return number;
        }

        public void increase() {
            this.count++;
        }

        public void decrease() {
            this.count--;
        }
    }
}
