package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.EssaySubmissionDTO;
import com.edu.eduplatform.dtos.QuestionAnswerDTO;
import com.edu.eduplatform.dtos.StudentAnswerDTO;
import com.edu.eduplatform.services.EssaySubmissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class EssaySubmissionController {

    @Autowired
    private EssaySubmissionService essaySubmissionService;


    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping("/submit/essay-quiz")
    public ResponseEntity<Void> saveQuizSubmission(@RequestBody EssaySubmissionDTO essaySubmissionDTO)
    {
        essaySubmissionService.saveQuizSubmission(essaySubmissionDTO);
        return ResponseEntity.ok().build();
    }


    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/getQuestionAnswersByQuizAndStudent")
    public ResponseEntity<List<QuestionAnswerDTO>> getQuestionAnswersByQuizIdAndStudentId(
            @RequestParam Long quizId, @RequestParam Long studentId) {
        List<QuestionAnswerDTO> questionAnswers = essaySubmissionService.getQuestionAnswersByQuizIdAndStudentId(quizId, studentId);
        return ResponseEntity.ok(questionAnswers);
    }


    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/getAnswers")
    public ResponseEntity<List<StudentAnswerDTO>> getStudentAnswers(@RequestParam Long quizId, @RequestParam Long studentId) {
        List<StudentAnswerDTO> answers = essaySubmissionService.getStudentAnswersByQuizAndStudent(quizId, studentId);
        return ResponseEntity.ok(answers);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/setOverAllGrade")
    public ResponseEntity<?> setOverAllGrade(@RequestParam Long quizId, @RequestParam Long studentId, @RequestParam double grade) {
        essaySubmissionService.setOverAllGrade(quizId, studentId, grade);
        return ResponseEntity.ok("Overall grade set successfully.");

    }


    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/getAnswerIds")
    public ResponseEntity<List<Long>> getStudentAnswerIds(@RequestParam Long quizId, @RequestParam Long studentId) {
        List<Long> answerIds = essaySubmissionService.getStudentAnswerIdsByQuizAndStudent(quizId, studentId);
        return ResponseEntity.ok(answerIds);
    }

    @PutMapping("/updateAnswerGrade")
    public ResponseEntity<?> updateAnswerGrade(@RequestParam Long answerId, @RequestParam int grade) {
        essaySubmissionService.updateAnswerGrade(answerId, grade);
        return ResponseEntity.ok("Grade updated successfully.");
    }




}
