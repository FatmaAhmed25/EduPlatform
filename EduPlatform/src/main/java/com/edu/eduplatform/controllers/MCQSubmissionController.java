package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.MCQSubmissionDTO;
import com.edu.eduplatform.models.MCQSubmission;
import com.edu.eduplatform.services.MCQSubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
public class MCQSubmissionController {

    @Autowired
    private MCQSubmissionService quizService;


    @PostMapping("/submit/mcq-quiz")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<MCQSubmission> submitQuiz(@RequestBody MCQSubmissionDTO quizSubmissionDTO) {
        MCQSubmission quizSubmission = quizService.submitQuiz(quizSubmissionDTO);
        return ResponseEntity.ok(quizSubmission);
    }

    @GetMapping("/submissions/{submissionId}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ROLE_STUDENT')")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<MCQSubmission> getQuizSubmission(@PathVariable Long submissionId) {
        MCQSubmission quizSubmission = quizService.getQuizSubmission(submissionId);
        return ResponseEntity.ok(quizSubmission);
    }
}
