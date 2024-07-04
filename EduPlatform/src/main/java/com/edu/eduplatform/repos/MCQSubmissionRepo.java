package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.MCQSubmission;
import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MCQSubmissionRepo extends JpaRepository<MCQSubmission ,Long> {
    MCQSubmission findByQuizQuizIdAndStudentUserID(Long studentId, Long quizId);

    Optional<MCQSubmission> findByQuizAndStudent(Quiz quiz, Student student);

}
