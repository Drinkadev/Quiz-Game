package com.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Question> questions = QuestionLoader.loadQuestions("questions.json");
        if (questions == null || questions.isEmpty()) {
            System.out.println("Não foi possível carregar as perguntas do quiz.");
            return;
        }

        QuizGame quizGame = new QuizGame(QuestionLoader.orderQuestionsByDifficulty(questions));
        quizGame.play();
    }
}
