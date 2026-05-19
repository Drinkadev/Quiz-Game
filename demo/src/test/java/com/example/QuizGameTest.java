package com.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuizGameTest {

    @Mock
    private Scanner mockScanner;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String getOutput() {
        return outContent.toString(StandardCharsets.UTF_8);
    }

    private Question easyQuestion(int answerIndex) {
        return new Question(
                "Pergunta de teste?",
                List.of("Opção A", "Opção B", "Opção C", "Opção D"),
                answerIndex,
                "fácil",
                "Dica de teste"
        );
    }

    private Question hardQuestion(int answerIndex) {
        return new Question(
                "Pergunta difícil?",
                List.of("Opção A", "Opção B", "Opção C", "Opção D"),
                answerIndex,
                "difícil",
                "Dica difícil"
        );
    }

    // ──────────────────────────────────────────────────────
    // Testes de resposta correta e incorreta
    // ──────────────────────────────────────────────────────

    @Test
    void play_withCorrectAnswer_printsCorrectMessage() {
        // Resposta correta: opção 1 (índice 0)
        when(mockScanner.nextLine()).thenReturn("1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("correta"));
    }

    @Test
    void play_withWrongAnswer_printsEliminationMessage() {
        // Resposta errada: opção 2 quando correta é 1 (índice 0)
        when(mockScanner.nextLine()).thenReturn("2");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("eliminado"));
    }

    @Test
    void play_withWrongAnswer_showsCorrectOptionName() {
        // O jogo deve exibir qual era a resposta certa
        when(mockScanner.nextLine()).thenReturn("2");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("Opção A")); // índice 0 = "Opção A"
    }

    @Test
    void play_withWrongAnswer_doesNotPrintVictory() {
        when(mockScanner.nextLine()).thenReturn("2");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertFalse(getOutput().contains("Ganhou"));
    }

    @Test
    void play_withAllCorrectAnswers_printsVictoryMessage() {
        // Dois perguntas, ambas com resposta na opção 1 (índice 0)
        when(mockScanner.nextLine()).thenReturn("1", "1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0), easyQuestion(0)), mockScanner);
        game.play();

        // Arte ASCII da vitória contém "$$" (parte do texto "Ganhou")
        assertTrue(getOutput().contains("$$"));
    }

    @Test
    void play_withWrongAnswerOnSecondQuestion_stopsEarly() {
        // Primeira certa, segunda errada
        when(mockScanner.nextLine()).thenReturn("1", "3");

        Question q1 = easyQuestion(0); // resposta: índice 0 → opção 1
        Question q2 = easyQuestion(1); // resposta: índice 1 → opção 2
        QuizGame game = new QuizGame(List.of(q1, q2), mockScanner);
        game.play();

        assertTrue(getOutput().contains("eliminado"));
        assertFalse(getOutput().contains("$$")); // sem vitória
    }

    // ──────────────────────────────────────────────────────
    // Testes de entrada inválida
    // ──────────────────────────────────────────────────────

    @Test
    void play_withInvalidTextInput_promptsForValidInput() {
        // Entrada inválida ("abc"), depois resposta correta
        when(mockScanner.nextLine()).thenReturn("abc", "1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("válido"));
    }

    @Test
    void play_withOutOfRangeNumber_promptsAgain() {
        // Número fora do intervalo (99), depois resposta correta
        when(mockScanner.nextLine()).thenReturn("99", "1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("válido"));
    }

    // ──────────────────────────────────────────────────────
    // Testes da ajuda: Dica (H)
    // ──────────────────────────────────────────────────────

    @Test
    void play_withHint_showsHintText() {
        // Usa dica ("H") e depois responde
        when(mockScanner.nextLine()).thenReturn("H", "1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("Dica de teste"));
    }

    @Test
    void play_withHintUsedTwice_showsAlreadyUsedMessage() {
        // Usa dica duas vezes: na segunda deve avisar que já foi usada
        when(mockScanner.nextLine()).thenReturn("H", "H", "1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("anteriormente"));
    }

    // ──────────────────────────────────────────────────────
    // Testes da ajuda: 50/50 (X)
    // ──────────────────────────────────────────────────────

    @Test
    void play_withFiftyFifty_showsUsedFiftyMessage() {
        // Usa 50/50 e depois responde
        when(mockScanner.nextLine()).thenReturn("X", "1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("50/50"));
    }

    @Test
    void play_withFiftyFiftyUsedTwice_showsAlreadyUsedMessage() {
        when(mockScanner.nextLine()).thenReturn("X", "X", "1");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("anteriormente"));
    }

    // ──────────────────────────────────────────────────────
    // Testes da ajuda: Pular (P)
    // ──────────────────────────────────────────────────────

    @Test
    void play_withSkip_printsSkippedMessage() {
        when(mockScanner.nextLine()).thenReturn("P");

        QuizGame game = new QuizGame(List.of(easyQuestion(0)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("pulada"));
    }

    @Test
    void play_withSkipUsedTwice_showsAlreadyUsedMessage() {
        // Primeira pergunta: pula. Segunda: tenta pular de novo → aviso. Depois responde.
        when(mockScanner.nextLine()).thenReturn("P", "P", "2");

        QuizGame game = new QuizGame(List.of(easyQuestion(0), easyQuestion(1)), mockScanner);
        game.play();

        assertTrue(getOutput().contains("anteriormente"));
    }

    // ──────────────────────────────────────────────────────
    // Testes da sequência de acertos (bônus)
    // ──────────────────────────────────────────────────────

    @Test
    void play_withFiveConsecutiveCorrectAnswers_grantsBonusHelp() {
        // 5 perguntas fáceis, todas respondidas corretamente sem usar ajuda
        when(mockScanner.nextLine()).thenReturn("1", "1", "1", "1", "1");

        List<Question> questions = List.of(
                easyQuestion(0), easyQuestion(0), easyQuestion(0),
                easyQuestion(0), easyQuestion(0)
        );

        QuizGame game = new QuizGame(questions, mockScanner);
        game.play();

        assertTrue(getOutput().contains("5 perguntas"));
    }

    @Test
    void play_withHardQuestionCorrect_doesNotIncrementStreak() {
        // Perguntas difíceis não contam para a sequência de bônus
        // Com 5 difíceis corretas, não deve aparecer mensagem de bônus
        when(mockScanner.nextLine()).thenReturn("1", "1", "1", "1", "1");

        List<Question> questions = List.of(
                hardQuestion(0), hardQuestion(0), hardQuestion(0),
                hardQuestion(0), hardQuestion(0)
        );

        QuizGame game = new QuizGame(questions, mockScanner);
        game.play();

        assertFalse(getOutput().contains("5 perguntas"));
    }

    // ──────────────────────────────────────────────────────
    // Testes de isHardDifficulty (visibilidade de pacote)
    // ──────────────────────────────────────────────────────

    @Test
    void isHardDifficulty_withDifficultAccented_returnsTrue() {
        QuizGame game = new QuizGame(List.of(), mockScanner);
        Question q = new Question("?", List.of("a"), 0, "difícil", "h");
        assertTrue(game.isHardDifficulty(q));
    }

    @Test
    void isHardDifficulty_withDifficultWithoutAccent_returnsTrue() {
        QuizGame game = new QuizGame(List.of(), mockScanner);
        Question q = new Question("?", List.of("a"), 0, "dificil", "h");
        assertTrue(game.isHardDifficulty(q));
    }

    @Test
    void isHardDifficulty_withEasyDifficulty_returnsFalse() {
        QuizGame game = new QuizGame(List.of(), mockScanner);
        Question q = new Question("?", List.of("a"), 0, "fácil", "h");
        assertFalse(game.isHardDifficulty(q));
    }

    @Test
    void isHardDifficulty_withMediumDifficulty_returnsFalse() {
        QuizGame game = new QuizGame(List.of(), mockScanner);
        Question q = new Question("?", List.of("a"), 0, "média", "h");
        assertFalse(game.isHardDifficulty(q));
    }
}
