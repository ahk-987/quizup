package com.quizup.backend.cli;

import java.util.List;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.quizup.backend.entity.Question;
import com.quizup.backend.entity.Quiz;
import com.quizup.backend.service.QuizService;

@Component
public class QuizCliRunner implements CommandLineRunner {

    private final QuizService quizService;
    private final Scanner scanner = new Scanner(System.in);

    public QuizCliRunner(QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    public void run(String... args) {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createQuiz();
                    case "2" -> addQuestion();
                    case "3" -> viewQuiz();
                    case "4" -> listAllQuizzes();
                    case "5" -> renameQuiz();
                    case "6" -> deleteQuestion();
                    case "7" -> deleteQuiz();
                    case "0" -> running = false;
                    default -> System.out.println("Not a valid option, try again.");
                }
            } catch (IllegalArgumentException e) {
                // Catches the "no quiz/question with that id" errors thrown by QuizService,
                // so a typo doesn't crash the whole CLI — just reports it and loops back to the menu.
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Bye.");
    }

    private void printMenu() {
        System.out.println("""

                === QuizUp CLI ===
                1. Create quiz
                2. Add question to quiz
                3. View quiz (with questions)
                4. List all quizzes
                5. Rename quiz
                6. Delete question
                7. Delete quiz
                0. Exit
                Choose:""");
    }

    private void createQuiz() {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Created by: ");
        String createdBy = scanner.nextLine();

        Quiz quiz = quizService.createQuiz(title, createdBy);
        System.out.println("Created quiz #" + quiz.getId() + " \"" + quiz.getTitle() + "\"");
    }

    private void addQuestion() {
        Long quizId = readLong("Quiz id: ");

        System.out.print("Question text: ");
        String text = scanner.nextLine();
        System.out.print("Option A: ");
        String a = scanner.nextLine();
        System.out.print("Option B: ");
        String b = scanner.nextLine();
        System.out.print("Option C: ");
        String c = scanner.nextLine();
        System.out.print("Option D: ");
        String d = scanner.nextLine();
        int correctIndex = (int) readLong("Correct option index (0=A, 1=B, 2=C, 3=D): ");
        int timeLimit = (int) readLong("Time limit (seconds): ");

        Quiz quiz = quizService.addQuestion(quizId, text, a, b, c, d, correctIndex, timeLimit);
        System.out.println("Added question. Quiz now has " + quiz.getQuestions().size() + " question(s).");
    }

    private void viewQuiz() {
        Long quizId = readLong("Quiz id: ");
        Quiz quiz = quizService.getQuiz(quizId);
        printQuiz(quiz);
    }

    private void listAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzesWithQuestions();
        if (quizzes.isEmpty()) {
            System.out.println("No quizzes yet.");
            return;
        }
        for (Quiz quiz : quizzes) {
            printQuiz(quiz);
        }
    }

    private void renameQuiz() {
        Long quizId = readLong("Quiz id: ");
        System.out.print("New title: ");
        String newTitle = scanner.nextLine();
        Quiz quiz = quizService.renameQuiz(quizId, newTitle);
        System.out.println("Renamed to \"" + quiz.getTitle() + "\"");
    }

    private void deleteQuestion() {
        Long quizId = readLong("Quiz id: ");
        Long questionId = readLong("Question id: ");
        quizService.deleteQuestion(quizId, questionId);
        System.out.println("Deleted question " + questionId + " from quiz " + quizId);
    }

    private void deleteQuiz() {
        Long quizId = readLong("Quiz id: ");
        quizService.deleteQuiz(quizId);
        System.out.println("Deleted quiz " + quizId);
    }

    private void printQuiz(Quiz quiz) {
        System.out.println("---");
        System.out.println("#" + quiz.getId() + " \"" + quiz.getTitle() + "\" (by " + quiz.getCreatedBy() + ")");
        List<Question> questions = quiz.getQuestions();
        if (questions.isEmpty()) {
            System.out.println("  (no questions yet)");
        } else {
            for (Question q : questions) {
                System.out.println("  [" + q.getId() + "] " + q.getText()
                        + " (correct: " + q.getCorrectOptionIndex() + ", " + q.getTimeLimitSeconds() + "s)");
            }
        }
    }

    private long readLong(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a number: ");
            }
        }
    }
}