package com.example;

import java.util.List;

public class Question {
    private final String question;
    private final List<String> options;
    private final int answer;
    private final String difficulty;
    private final String hint;

    public Question(String question, List<String> options, int answer, String difficulty, String hint) {
        this.question = question;
        this.options = options;
        this.answer = answer;
        this.difficulty = difficulty;
        this.hint = hint;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getAnswer() {
        return answer;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getHint() {
        return hint;
    }
}
