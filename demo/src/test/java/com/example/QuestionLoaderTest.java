package com.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class QuestionLoaderTest {

    // ──────────────────────────────────────────────────────
    // loadQuestions
    // ──────────────────────────────────────────────────────

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

    // ──────────────────────────────────────────────────────
    // orderQuestionsByDifficulty
    // ──────────────────────────────────────────────────────

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
    void orderQuestionsByDifficulty_putsMediumBetweenEasyAndHard() {
        Question easy = new Question("e", List.of("a", "b"), 0, "fácil", "h");
        Question medium = new Question("m", List.of("a", "b"), 0, "média", "h");
        Question hard = new Question("h", List.of("a", "b"), 0, "difícil", "h");

        List<Question> ordered = QuestionLoader.orderQuestionsByDifficulty(List.of(hard, medium, easy));

        boolean seenMediumOrHard = false;
        for (Question q : ordered) {
            String d = q.getDifficulty().toLowerCase();
            boolean isEasy = d.equals("fácil") || d.equals("facil");
            boolean isMedium = d.equals("média") || d.equals("media");
            if (isMedium) seenMediumOrHard = true;
            if (seenMediumOrHard) {
                assertFalse(isEasy, "Easy questions must appear before medium questions");
            }
        }

        boolean seenHard = false;
        for (Question q : ordered) {
            String d = q.getDifficulty().toLowerCase();
            boolean isMedium = d.equals("média") || d.equals("media");
            boolean isHard = d.equals("difícil") || d.equals("dificil");
            if (isHard) seenHard = true;
            if (seenHard) {
                assertFalse(isMedium, "Medium questions must appear before hard questions");
            }
        }
    }

    @Test
    void orderQuestionsByDifficulty_withEmptyList_returnsEmptyList() {
        List<Question> ordered = QuestionLoader.orderQuestionsByDifficulty(List.of());
        assertNotNull(ordered);
        assertTrue(ordered.isEmpty());
    }

    @Test
    void orderQuestionsByDifficulty_preservesAllQuestions() {
        Question easy = new Question("e", List.of("a", "b"), 0, "fácil", "h");
        Question medium = new Question("m", List.of("a", "b"), 0, "média", "h");
        Question hard = new Question("h", List.of("a", "b"), 0, "difícil", "h");

        List<Question> ordered = QuestionLoader.orderQuestionsByDifficulty(List.of(easy, medium, hard));

        assertEquals(3, ordered.size());
    }

    @Test
    void orderQuestionsByDifficulty_withUnknownDifficulty_treatsAsMedium() {
        Question unknown = new Question("u", List.of("a", "b"), 0, "desconhecido", "h");
        Question easy = new Question("e", List.of("a", "b"), 0, "fácil", "h");
        Question hard = new Question("h", List.of("a", "b"), 0, "difícil", "h");

        List<Question> ordered = QuestionLoader.orderQuestionsByDifficulty(List.of(hard, unknown, easy));

        // Desconhecido vai para a fila de médias — não deve aparecer antes das fáceis nem depois das difíceis
        int easyIdx = -1, unknownIdx = -1, hardIdx = -1;
        for (int i = 0; i < ordered.size(); i++) {
            String d = ordered.get(i).getDifficulty();
            if (d.equals("fácil")) easyIdx = i;
            else if (d.equals("desconhecido")) unknownIdx = i;
            else if (d.equals("difícil")) hardIdx = i;
        }

        assertTrue(easyIdx < unknownIdx, "Easy should come before unknown (treated as medium)");
        assertTrue(unknownIdx < hardIdx, "Unknown (medium) should come before hard");
    }

    // ──────────────────────────────────────────────────────
    // removeHalfWrongOptions
    // ──────────────────────────────────────────────────────

    @Test
    void removeHalfWrongOptions_keepsCorrectAndReducesCount() {
        List<Integer> visible = List.of(0, 1, 2, 3, 4, 5, 6);
        int correct = 3;

        List<Integer> reduced = QuestionLoader.removeHalfWrongOptions(visible, correct);

        assertTrue(reduced.contains(correct));
        assertTrue(reduced.size() < visible.size());
    }

    @Test
    void removeHalfWrongOptions_alwaysKeepsCorrectAnswer() {
        // Executa várias vezes para garantir que o embaralhamento não remove a correta
        List<Integer> visible = List.of(0, 1, 2, 3);
        int correct = 2;

        for (int i = 0; i < 30; i++) {
            List<Integer> reduced = QuestionLoader.removeHalfWrongOptions(visible, correct);
            assertTrue(reduced.contains(correct), "Correct answer must always be kept");
        }
    }

    @Test
    void removeHalfWrongOptions_withTwoOptions_removesOneWrong() {
        // Apenas 1 certa + 1 errada: deve remover a errada e manter só a certa
        List<Integer> visible = List.of(0, 1);
        int correct = 0;

        List<Integer> reduced = QuestionLoader.removeHalfWrongOptions(visible, correct);

        assertTrue(reduced.contains(correct));
        assertEquals(1, reduced.size());
    }

    @Test
    void removeHalfWrongOptions_resultIsSubsetOfOriginal() {
        List<Integer> visible = List.of(0, 1, 2, 3);
        int correct = 1;

        List<Integer> reduced = QuestionLoader.removeHalfWrongOptions(visible, correct);

        assertTrue(visible.containsAll(reduced), "Result must be a subset of the original options");
    }
}
