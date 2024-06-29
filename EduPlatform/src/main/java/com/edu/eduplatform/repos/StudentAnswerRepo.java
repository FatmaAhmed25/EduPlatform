package com.edu.eduplatform.repos;


import com.edu.eduplatform.models.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentAnswerRepo extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByEssaySubmissionId(Long quizSubmissionId);
}
