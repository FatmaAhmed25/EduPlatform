package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.models.QuizSubmission;
import com.edu.eduplatform.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuizSubmissionRepo extends JpaRepository<QuizSubmission,Long> {
    boolean existsByQuizAndStudent(Quiz quiz, Student student);

    List<QuizSubmission> findByStudentUserID(Long studentId);

    List<QuizSubmission> findByQuiz_QuizId(Long quizId);

    int countByQuiz_QuizId(Long quizId);

    int countByQuiz_QuizIdAndCheatingStatusNot(Long quizId, QuizSubmission.CheatingStatus cheatingStatus);
}
