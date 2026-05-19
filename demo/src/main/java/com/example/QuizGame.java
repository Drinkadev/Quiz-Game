package com.example;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QuizGame {
    private final List<Question> questions;
    private final Scanner scanner;
    private final HelpState helps;
    private int score;
    private int correctStreakWithoutHint;

    public QuizGame(List<Question> questions) {
        this.questions = questions;
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        this.helps = new HelpState();
        this.correctStreakWithoutHint = 0;
    }

    public void play() {
        TerminalUtils.clearScreen();
        printWelcomeMessage();

        int total = questions.size();

        for (int i = 0; i < total; i++) {
            TerminalUtils.clearScreen();
            printWelcomeMessage();
            Question question = questions.get(i);
            printQuestionHeader(i + 1, total, question);
            printQuestionText(question);

            List<Integer> visibleOptions = buildOptionIndexes(question.getOptions().size());
            boolean hintWasAvailableBeforeQuestion = helps.isHintAvailable();
            int selectedOption = askQuestion(question, visibleOptions, i + 1, total);

            if (selectedOption < 0) {
                System.out.println("Pergunta pulada.\n");
                correctStreakWithoutHint = 0;
                continue;
            }

            if (selectedOption == question.getAnswer()) {
                printCorrectMessage();
                score++;
                
                boolean hintUsedDuringQuestion = hintWasAvailableBeforeQuestion && !helps.isHintAvailable();
                if (!isHardDifficulty(question) && !hintUsedDuringQuestion) {
                    correctStreakWithoutHint++;
                    if (correctStreakWithoutHint >= 5) {
                        helps.resetHint();
                        System.out.println("\n🎉 Parabéns! Você acertou 5 perguntas seguidas sem usar dica!");
                        System.out.println("Ganhou uma dica aleatória!");
                        TerminalUtils.sleep(2000);
                        correctStreakWithoutHint = 0;
                    }
                } else {
                    correctStreakWithoutHint = 0;
                }
            } else {
                correctStreakWithoutHint = 0;
                printErrorMessage(question);
                scanner.close();
                return;
            }
        }

        if (score == total) {
            printVictoryMessage();
        }
        scanner.close();
    }

    private void printWelcomeMessage() {
        System.out.println("=== Show Do Milhão ===");
        System.out.println("As perguntas virão em ordem: fácil, média e difícil.\n");
    }

    private void printQuestionHeader(int index, int total, Question question) {
        System.out.printf("Pergunta %d/%d (%s):%n", index, total, capitalize(question.getDifficulty()));
    }

    private void printQuestionText(Question question) {
        System.out.println(question.getQuestion());
    }

    private List<Integer> buildOptionIndexes(int size) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            indexes.add(i);
        }
        return indexes;
    }

    private int askQuestion(Question question, List<Integer> visibleOptions, int currentIndex, int total) {
        while (true) {
            printOptions(question, visibleOptions);
            printHelpStatus();
            System.out.print("Digite o número da resposta ou H/X/P para usar ajuda: ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("H") || line.equalsIgnoreCase("X") || line.equalsIgnoreCase("P")) {
                if (helps.noHelpsLeft()) {
                    TerminalUtils.clearScreen();
                    redrawQuestion(question, currentIndex, total, visibleOptions);
                    System.out.println("Você está desamparado. Nenhuma ajuda disponível.");
                    TerminalUtils.sleep(2000);
                    continue;
                }
            }

            if (line.equalsIgnoreCase("H")) {
                if (!helps.isHintAvailable()) {
                    TerminalUtils.clearScreen();
                    redrawQuestion(question, currentIndex, total, visibleOptions);
                    System.out.println("Você já usou a dica anteriormente.\n");
                    continue;
                }
                helps.useHint();
                TerminalUtils.clearScreen();
                redrawQuestion(question, currentIndex, total, visibleOptions);
                System.out.printf("Você usou a dica. Ajudas restantes: %s%n%n", helps.formatAvailableHelps());
                System.out.printf("Dica: %s%n%n", question.getHint());
                continue;
            }

            if (line.equalsIgnoreCase("X")) {
                if (!helps.isFiftyAvailable()) {
                    TerminalUtils.clearScreen();
                    redrawQuestion(question, currentIndex, total, visibleOptions);
                    System.out.println("Você já usou a ajuda 50/50 anteriormente.\n");
                    continue;
                }
                if (visibleOptions.size() <= 2) {
                    TerminalUtils.clearScreen();
                    redrawQuestion(question, currentIndex, total, visibleOptions);
                    System.out.println("Não há opções suficientes para usar 50/50.\n");
                    continue;
                }
                helps.useFifty();
                visibleOptions = QuestionLoader.removeHalfWrongOptions(visibleOptions, question.getAnswer());
                TerminalUtils.clearScreen();
                redrawQuestion(question, currentIndex, total, visibleOptions);
                System.out.printf("Você usou 50/50. Ajudas restantes: %s%n%n", helps.formatAvailableHelps());
                continue;
            }

            if (line.equalsIgnoreCase("P")) {
                if (!helps.isSkipAvailable()) {
                    TerminalUtils.clearScreen();
                    redrawQuestion(question, currentIndex, total, visibleOptions);
                    System.out.println("Você já usou pular pergunta anteriormente.\n");
                    continue;
                }
                helps.useSkip();
                TerminalUtils.clearScreen();
                redrawQuestion(question, currentIndex, total, visibleOptions);
                System.out.printf("Você usou pular pergunta. Ajudas restantes: %s%n%n", helps.formatAvailableHelps());
                TerminalUtils.sleep(2000);
                return -1;
            }

            try {
                int optionNumber = Integer.parseInt(line);
                if (optionNumber >= 1 && optionNumber <= visibleOptions.size()) {
                    return visibleOptions.get(optionNumber - 1);
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.printf("Por favor digite um número válido entre 1 e %d, ou H/X/P.%n%n", visibleOptions.size());
        }
    }

    private void redrawQuestion(Question question, int currentIndex, int total, List<Integer> visibleOptions) {
        printWelcomeMessage();
        printQuestionHeader(currentIndex, total, question);
        printQuestionText(question);
        printOptions(question, visibleOptions);
        printHelpStatus();
    }

    private void printOptions(Question question, List<Integer> visibleOptions) {
        List<String> options = question.getOptions();
        for (int i = 0; i < visibleOptions.size(); i++) {
            System.out.printf("  %d) %s%n", i + 1, options.get(visibleOptions.get(i)));
        }
    }

    private void printHelpStatus() {
        System.out.printf("[H] Dica (%s)  [X] 50/50 (%s)  [P] Pular (%s)%n",
                helps.isHintAvailable() ? "disponível" : "usada",
                helps.isFiftyAvailable() ? "disponível" : "usada",
                helps.isSkipAvailable() ? "disponível" : "usada");
    }

    private void printCorrectMessage() {
        System.out.print("Boa! Resposta correta.\n\n");
    }

    private void printErrorMessage(Question question) {
        System.out.print("COLQUE AQUI SUA MENSAGEM DE ERRO");
        System.out.printf("Resposta errada. A resposta certa era: %s%n%n", question.getOptions().get(question.getAnswer()));
    }

    private void printVictoryMessage() {
        System.out.print("COLQUE AQUI SUA MENSAGEM DE VITORIA");
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private boolean isHardDifficulty(Question question) {
        String difficulty = question.getDifficulty().toLowerCase();
        return difficulty.equals("difícil") || difficulty.equals("dificil");
    }
}
