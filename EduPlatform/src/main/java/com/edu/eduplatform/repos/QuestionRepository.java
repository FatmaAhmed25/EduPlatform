package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizQuizId(Long quizId);
}