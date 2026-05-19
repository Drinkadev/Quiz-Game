package com.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionTest {

    @Test
    void getters_returnConstructorValues() {
        List<String> options = List.of("A", "B", "C");
        Question q = new Question("text", options, 1, "fácil", "hint");

        assertEquals("text", q.getQuestion());
        assertEquals(options, q.getOptions());
        assertEquals(1, q.getAnswer());
        assertEquals("fácil", q.getDifficulty());
        assertEquals("hint", q.getHint());
    }
}

