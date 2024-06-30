package com.edu.eduplatform.repos;


import com.edu.eduplatform.models.StudentEssayAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAnswerRepo extends JpaRepository<StudentEssayAnswer, Long> {

    List<StudentEssayAnswer> findByEssaySubmissionId(Long quizSubmissionId);
}
