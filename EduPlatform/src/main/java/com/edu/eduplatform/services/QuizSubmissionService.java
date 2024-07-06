package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.GetSubmissionsInstructorResponse;
import com.edu.eduplatform.dtos.PastQuizSubmissionStudent;
import com.edu.eduplatform.models.QuizSubmission;
import com.edu.eduplatform.repos.QuizSubmissionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizSubmissionService {

    @Autowired
    private QuizSubmissionRepo quizSubmissionRepo;

    public List<PastQuizSubmissionStudent> getQuizSubmissionsByStudentId(Long studentId) {
        List<QuizSubmission> allSubmissions = quizSubmissionRepo.findByStudentUserID(studentId);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

        return allSubmissions.stream()
                .filter(submission -> submission.getQuiz().getEndTime().isBefore(now))
                .map(submission -> {
                    Long courseId = submission.getQuiz().getCourse().getCourseId();
                    Long quizId = submission.getQuiz().getQuizId();
                    String courseName = submission.getQuiz().getCourse().getTitle();
                    String quizTitle = submission.getQuiz().getTitle();
                    return new PastQuizSubmissionStudent(courseId, quizId, courseName, quizTitle, submission);
                })
                .collect(Collectors.toList());
    }

    public List<GetSubmissionsInstructorResponse> getSubmissionsByQuiz(Long quizId) {
        return quizSubmissionRepo.findByQuiz_QuizId(quizId).stream()
                .map(submission -> new GetSubmissionsInstructorResponse(
                        submission.getStudent().getUserID(),
                        submission.getQuiz().getCourse().getCourseId(),
                        submission.getQuiz().getQuizId(),
                        submission.getStudent().getUsername(),
                        submission))
                .collect(Collectors.toList());
    }

    public int getCheatingSubmissionCountByQuiz(Long quizId) {
        return quizSubmissionRepo.countByQuiz_QuizIdAndCheatingStatusNot(quizId, QuizSubmission.CheatingStatus.PASSED);
    }

    public int getSubmissionCountByQuiz(Long quizId) {
        return quizSubmissionRepo.countByQuiz_QuizId(quizId);
    }


}
