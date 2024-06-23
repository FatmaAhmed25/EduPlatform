package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}