package com.quizup.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quizup.backend.entity.Question;
import com.quizup.backend.entity.Quiz;
import com.quizup.backend.repository.QuizRepository;

@Service
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz createQuiz(String title, String createdBy) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setCreatedBy(createdBy);
        return quizRepository.save(quiz);
    }

    public Quiz addQuestion(Long quizId, String text,
                             String optionA, String optionB,
                             String optionC, String optionD,
                             int correctOptionIndex, int timeLimitSeconds) {

        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new IllegalArgumentException("No quiz with id " + quizId));

        Question question = new Question();
        question.setText(text);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrectOptionIndex(correctOptionIndex);
        question.setTimeLimitSeconds(timeLimitSeconds);

        quiz.addQuestion(question);
        return quizRepository.save(quiz);
    }

    @Transactional(readOnly = true)
    public Quiz getQuiz(Long id) {
        return quizRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new IllegalArgumentException("No quiz with id " + id));
    }

    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzesWithQuestions() {
        return quizRepository.findAllWithQuestions();
    }

    public Quiz renameQuiz(Long id, String newTitle) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No quiz with id " + id));
        quiz.setTitle(newTitle);
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException("No quiz with id " + id);
        }
        quizRepository.deleteById(id);
    }

    public void deleteQuestion(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new IllegalArgumentException("No quiz with id " + quizId));

        boolean removed = quiz.getQuestions().removeIf(q -> q.getId().equals(questionId));
        if (!removed) {
            throw new IllegalArgumentException("No question with id " + questionId + " in quiz " + quizId);
        }
        quizRepository.save(quiz);
    }
}