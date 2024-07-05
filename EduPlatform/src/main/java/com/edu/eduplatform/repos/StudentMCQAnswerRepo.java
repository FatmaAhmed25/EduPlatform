package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Answer;
import com.edu.eduplatform.models.Question;
import com.edu.eduplatform.models.StudentMCQAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentMCQAnswerRepo extends JpaRepository<StudentMCQAnswer, Long> {
    List<StudentMCQAnswer> findByAnswer(Answer answer);

    List<StudentMCQAnswer> findByQuestion(Question question);
}