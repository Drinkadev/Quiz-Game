package com.example;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Question> questions = loadQuestions("questions.json");
        if (questions == null || questions.isEmpty()) {
            System.out.println("Não foi possível carregar as perguntas do quiz.");
            return;
        }

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.println("=== Quiz Terminal ===");
        System.out.println("Responda as perguntas digitando o número da opção e pressione Enter.\n");

        int score = 0;
        int total = questions.size();

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.printf("Pergunta %d/%d:%n", i + 1, total);
            System.out.println(question.question());

            List<String> options = question.options();
            for (int j = 0; j < options.size(); j++) {
                System.out.printf("  %d) %s%n", j + 1, options.get(j));
            }

            int selected = askOption(scanner, options.size());
            if (selected == question.answer() + 1) {
                System.out.println("Resposta certa!\n");
                score++;
            } else {
                System.out.printf("Resposta errada. A resposta certa era: %s%n%n", options.get(question.answer()));
            }
        }

        System.out.println("=== Resultado Final ===");
        System.out.printf("Você acertou %d de %d perguntas.%n", score, total);
        System.out.printf("Sua nota: %.0f%%%n", total > 0 ? (score * 100.0 / total) : 0);
        scanner.close();
    }

    private static List<Question> loadQuestions(String resourceName) {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                return null;
            }
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            return new Gson().fromJson(reader, new TypeToken<List<Question>>() {
            }.getType());
        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo de perguntas: " + e.getMessage());
            return null;
        }
    }

    private static int askOption(Scanner scanner, int numberOfOptions) {
        while (true) {
            System.out.print("Digite o número da resposta: ");
            String line = scanner.nextLine().trim();
            try {
                int option = Integer.parseInt(line);
                if (option >= 1 && option <= numberOfOptions) {
                    return option;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.printf("Por favor digite um número entre 1 e %d.%n", numberOfOptions);
        }
    }

    private static record Question(String question, List<String> options, int answer) {
    }
}