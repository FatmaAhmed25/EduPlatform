package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.GenerateQuizDTO;
import com.edu.eduplatform.dtos.QuizDTO;
import com.edu.eduplatform.dtos.QuizForStudentDTO;
import com.edu.eduplatform.models.Answer;
import com.edu.eduplatform.models.Question;
import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.services.QuizService;
import com.edu.eduplatform.utils.quiz.pdf.PdfService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.edu.eduplatform.annotations.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
@PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/generate-submission-pdf/{studentId}/{quizId}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ROLE_STUDENT')")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> generateStudentSubmissionPdf(@PathVariable Long studentId, @PathVariable Long quizId) {
        try {
            byte[] pdfBytes = pdfService.generateStudentSubmissionPdf(studentId, quizId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "student_submission.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate student submission PDF: " + e.getMessage());
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @Operation(summary = "Generate PDF of a quiz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generated successfully", content = {
                    @Content(mediaType = "application/pdf")
            }),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/generate-pdf/{quizId}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long quizId) {
        try {
            byte[] pdfBytes = pdfService.generateQuizPdf(quizId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "quiz.pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizDTO quizDTO) {
        Quiz createdQuiz = quizService.createQuiz(quizDTO);
        return ResponseEntity.ok(createdQuiz);
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

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/{instructorId}/{courseId}/{quizId}/instructor")
    @ValidateInstructorBelongsToCourse
    public ResponseEntity<Quiz> getQuizForInstructor(@PathVariable @ValidateInstructor Long instructorId ,@PathVariable @ValidateCourse Long courseId,@PathVariable @ValidateQuiz Long quizId) {
        try {
            Quiz quiz = quizService.getQuizByIdForInstructor(quizId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/{studentId}/{courseId}/{quizId}/student")
    public ResponseEntity<QuizForStudentDTO> getQuizForStudent(@PathVariable @ValidateStudent Long studentId ,@PathVariable @ValidateCourse Long courseId,@PathVariable @ValidateQuiz Long quizId) {
        try {
            QuizForStudentDTO quizDTO = quizService.getQuizForStudentById(quizId);
            return ResponseEntity.ok(quizDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }


//    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR') or hasAuthority('ROLE_STUDENT')")
//    @SecurityRequirement(name="BearerAuth")
//    @GetMapping("/course/{instructorId}/{courseId}")
//    public List<Quiz> getQuizzesByCourseId(@PathVariable @ValidateInstructor ,@PathVariable @ValidateCourse Long courseId) {
//        return quizService.getQuizzesByCourseId(courseId);
//    }

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @SecurityRequirement(name="BearerAuth")
    @PostMapping(value = "/generateMcq" , consumes = {"multipart/form-data"})
    @ValidateInstructorBelongsToCourse
    public ResponseEntity<?> generateQuiz(@RequestParam @ValidateInstructor Long instructorId,
                                          @RequestParam @ValidateCourse Long courseId,
                                          @RequestParam String quizTitle,
                                          @RequestParam String startTime,
                                          @RequestParam String endTime,
                                          @RequestParam int numOfQuestions,
                                          @RequestParam("pdfFiles") List<MultipartFile> pdfFiles) {
        GenerateQuizDTO requestDTO = new GenerateQuizDTO(courseId,quizTitle,startTime,endTime,numOfQuestions,pdfFiles);
        Quiz createdQuiz = quizService.generateAndCreateMcqQuiz(requestDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @SecurityRequirement(name="BearerAuth")
    @PostMapping(value = "/generateEssay" , consumes = {"multipart/form-data"})
    @ValidateInstructorBelongsToCourse
    public ResponseEntity<?> generateEssayQuiz(@RequestParam @ValidateInstructor Long instructorId,
                                          @RequestParam @ValidateCourse Long courseId,
                                          @RequestParam String quizTitle,
                                          @RequestParam String startTime,
                                          @RequestParam String endTime,
                                          @RequestParam int numOfQuestions,
                                          @RequestParam("pdfFiles") List<MultipartFile> pdfFiles) {
        GenerateQuizDTO requestDTO = new GenerateQuizDTO(courseId,quizTitle,startTime,endTime,numOfQuestions,pdfFiles);
        Quiz createdQuiz = quizService.generateEssayQuiz(requestDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

}
