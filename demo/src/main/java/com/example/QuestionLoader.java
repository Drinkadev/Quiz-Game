package com.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionLoader {
    public static List<Question> loadQuestions(String resourceName) {
        try (InputStream inputStream = QuestionLoader.class.getClassLoader().getResourceAsStream(resourceName)) {
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

    public static List<Question> orderQuestionsByDifficulty(List<Question> questions) {
        List<Question> easy = new ArrayList<>();
        List<Question> medium = new ArrayList<>();
        List<Question> hard = new ArrayList<>();

        for (Question question : questions) {
            switch (question.getDifficulty().toLowerCase()) {
                case "fácil", "facil" -> easy.add(question);
                case "média", "media" -> medium.add(question);
                case "difícil", "dificil" -> hard.add(question);
                default -> medium.add(question);
            }
        }

        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        List<Question> ordered = new ArrayList<>();
        ordered.addAll(easy);
        ordered.addAll(medium);
        ordered.addAll(hard);
        return ordered;
    }

    public static List<Integer> removeHalfWrongOptions(List<Integer> visibleOptions, int correctAnswer) {
        List<Integer> wrongOptions = new ArrayList<>();
        for (int option : visibleOptions) {
            if (option != correctAnswer) {
                wrongOptions.add(option);
            }
        }

        Collections.shuffle(wrongOptions);
        int removeCount = Math.max(1, wrongOptions.size() / 2);
        List<Integer> reduced = new ArrayList<>();
        Set<Integer> toRemove = new HashSet<>(wrongOptions.subList(0, removeCount));

        for (int option : visibleOptions) {
            if (!toRemove.contains(option)) {
                reduced.add(option);
            }
        }
        return reduced;
    }
}
