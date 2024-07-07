package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCourseCourseId(Long courseId);
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.quizId = :quizId")
    Quiz findByQuizIdWithQuestions(@Param("quizId") Long quizId);

    boolean existsByQuizId(Long quizId);

    Collection<Quiz> findByCourse_Students_UserID(Long studentId);

    Collection<Quiz> findByCourse_CourseId(Long courseId);
    @Query("SELECT DISTINCT q FROM Quiz q " +
            "JOIN q.questions ques " +
            "JOIN QuizSubmission sub ON sub.quiz = q " +
            "JOIN EssaySubmission es ON es.id = sub.id " +
            "WHERE q.course.courseId = :courseId AND es.totalGrade IS NULL")
    List<Quiz> findQuizzesWithNullEssaySubmissions(@Param("courseId") Long courseId);

}
