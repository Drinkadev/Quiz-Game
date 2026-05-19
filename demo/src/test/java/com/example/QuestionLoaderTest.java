package com.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class QuestionLoaderTest {


    @Test
    void loadQuestions_success() {
        List<Question> questions = QuestionLoader.loadQuestions("questions.json");
        assertNotNull(questions);
        assertFalse(questions.isEmpty());
    }

    @Test
    void loadQuestions_returnsNullForMissingFile() {
        List<Question> questions = QuestionLoader.loadQuestions("file-not-found.json");
        assertNull(questions);
    }

    @Test
    void orderQuestionsByDifficulty_sortsAllEasyBeforeHard() {
        Question easy1 = new Question("e1", List.of("a", "b", "c"), 0, "fácil", "h1");
        Question easy2 = new Question("e2", List.of("a", "b", "c"), 0, "facil", "h2");
        Question hard1 = new Question("h1", List.of("a", "b", "c"), 0, "difícil", "h3");
        Question hard2 = new Question("h2", List.of("a", "b", "c"), 0, "dificil", "h4");
        Question medium1 = new Question("m1", List.of("a", "b", "c"), 0, "média", "h5");

        List<Question> input = List.of(hard1, medium1, hard2, easy2, easy1);
        List<Question> ordered = QuestionLoader.orderQuestionsByDifficulty(input);

        boolean seenHardOrLater = false;
        for (Question q : ordered) {
            String d = q.getDifficulty().toLowerCase();
            boolean isEasy = d.equals("fácil") || d.equals("facil");
            boolean isHard = d.equals("difícil") || d.equals("dificil");
            if (isHard) {
                seenHardOrLater = true;
            }
            if (seenHardOrLater) {
                assertFalse(isEasy, "Easy questions must appear before hard questions");
            }
        }
    }

    @Test
    void removeHalfWrongOptions_keepsCorrectAndReducesCount() {
        List<Integer> visible = List.of(0, 1, 2, 3, 4, 5, 6);
        int correct = 3;

        List<Integer> reduced = QuestionLoader.removeHalfWrongOptions(visible, correct);

        assertTrue(reduced.contains(correct));
        assertTrue(reduced.size() < visible.size());
    }
}

