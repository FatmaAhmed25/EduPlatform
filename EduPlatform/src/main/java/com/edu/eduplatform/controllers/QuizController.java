package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.QuizDTO;
import com.edu.eduplatform.models.Answer;
import com.edu.eduplatform.models.Question;
import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.services.QuizService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
public class QuizController {

    @Autowired
    private QuizService quizService;
    @PostMapping
    @SecurityRequirement(name="BearerAuth")
    public Quiz createQuiz(@RequestBody QuizDTO quizDTO) {
        return quizService.createQuiz(quizDTO);
    }


    @GetMapping("/{quizId}/questions")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<List<String>> getQuestionsForQuiz(@PathVariable Long quizId) {
        List<String> questions = quizService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/{quizId}/questions")
    @SecurityRequirement(name="BearerAuth")
    public Question addQuestionToQuiz(@PathVariable Long quizId, @RequestBody Question question) {
        return quizService.addQuestionToQuiz(quizId, question);
    }

    @PostMapping("/questions/{questionId}/answers")
    @SecurityRequirement(name="BearerAuth")
    public Answer addAnswerToQuestion(@PathVariable Long questionId, @RequestBody Answer answer) {
        return quizService.addAnswerToQuestion(questionId, answer);
    }

    @GetMapping
    @SecurityRequirement(name="BearerAuth")
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ROLE_STUDENT')")
    @SecurityRequirement(name="BearerAuth")
    @GetMapping("/{quizId}")
    public Quiz getQuiz(@PathVariable Long quizId) {
        return quizService.getQuizById(quizId);
    }


    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ROLE_STUDENT')")
    @SecurityRequirement(name="BearerAuth")
    @GetMapping("/course/{courseId}")
    public List<Quiz> getQuizzesByCourseId(@PathVariable Long courseId) {
        return quizService.getQuizzesByCourseId(courseId);
    }
}
