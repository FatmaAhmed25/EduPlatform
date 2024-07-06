package com.edu.eduplatform.controllers;

import com.edu.eduplatform.annotations.ValidateInstructor;
import com.edu.eduplatform.annotations.ValidateQuiz;
import com.edu.eduplatform.annotations.ValidateStudent;
import com.edu.eduplatform.dtos.GetSubmissionsInstructorResponse;
import com.edu.eduplatform.dtos.PastQuizSubmissionStudent;
import com.edu.eduplatform.services.QuizSubmissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submissions")
public class QuizSubmissionController {

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/get-cheating-count/{quizId}")
    public int getCheatingSubmissionCountByQuiz(@PathVariable Long quizId)
    {
        return quizSubmissionService.getCheatingSubmissionCountByQuiz(quizId);
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority( 'ROLE_STUDENT')")
    @GetMapping("/quiz-submissions/student/{studentId}")
    public List<PastQuizSubmissionStudent> getSubmissionsByStudentId(@PathVariable @ValidateStudent Long studentId) {
        return quizSubmissionService.getQuizSubmissionsByStudentId(studentId);
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/instructor/quiz/{instructorId}/{quizId}")
    public List<GetSubmissionsInstructorResponse> getSubmissionsByCourseAndInstructor(@PathVariable @ValidateInstructor Long instructorId, @PathVariable @ValidateQuiz Long quizId) {
        return quizSubmissionService.getSubmissionsByQuiz(quizId);
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority( 'ROLE_INSTRUCTOR')")
    @GetMapping("/quiz/{quizId}/count")
    public int getSubmissionCountByQuiz(@PathVariable @ValidateQuiz Long quizId) {
        return quizSubmissionService.getSubmissionCountByQuiz(quizId);
    }



}
