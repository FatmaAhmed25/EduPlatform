package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizQuizId(Long quizId);

    @Query("SELECT q.text FROM Question q WHERE q.quiz.quizId = :quizId")
    List<String> findTextByQuizQuizId(Long quizId);


    @Query("SELECT q FROM Question q WHERE q.quiz.quizId = :quizId ORDER BY q.id ASC")
    List<Question> findAnyQuestionByQuizId(@Param("quizId") Long quizId);



}