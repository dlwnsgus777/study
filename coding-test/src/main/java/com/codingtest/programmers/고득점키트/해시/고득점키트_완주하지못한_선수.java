package com.codingtest.programmers.고득점키트.해시;

import java.util.Arrays;

public class 고득점키트_완주하지못한_선수 {
    public String solution(String[] participant, String[] completion) {
        Arrays.sort(participant);
        Arrays.sort(completion);

        String anwser = "";

        for (int i = 0; i < participant.length; i++) {
            if (i == completion.length) {
                anwser = participant[i];
                break;
            }

            if (!participant[i].equals(completion[i])) {
                anwser = participant[i];
            }
        }

        return anwser;
    }
}
