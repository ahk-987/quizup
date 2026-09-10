package com.quizup.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quizup.backend.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("select q from Quiz q left join fetch q.questions where q.id = :id")
    Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);

    @Query("select distinct q from Quiz q left join fetch q.questions")
    List<Quiz> findAllWithQuestions();
}