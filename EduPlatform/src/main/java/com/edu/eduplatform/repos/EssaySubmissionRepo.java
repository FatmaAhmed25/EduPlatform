package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.models.EssaySubmission;
import com.edu.eduplatform.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EssaySubmissionRepo extends JpaRepository<EssaySubmission, Long> {

    EssaySubmission findByQuizQuizIdAndStudentUserID(Long quizId, Long studentId);

    Optional<EssaySubmission> findByQuizAndStudent(Quiz quiz, Student student);
}