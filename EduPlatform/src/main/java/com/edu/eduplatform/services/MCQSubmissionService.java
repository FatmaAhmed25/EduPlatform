package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.MCQSubmissionDTO;

import com.edu.eduplatform.dtos.StudentMCQAnswerDTO;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MCQSubmissionService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private MCQSubmissionRepo quizSubmissionRepository;

    @Autowired
    private CheatingReportRepo cheatingReportRepo;

    public ResponseEntity<String> submitQuiz(MCQSubmissionDTO mcqQuizSubmissionDTO) {
        Student student = studentRepository.findById(mcqQuizSubmissionDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Quiz quiz = quizRepository.findById(mcqQuizSubmissionDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Optional<MCQSubmission> existingSubmission = quizSubmissionRepository.findByQuizAndStudent(quiz, student);
        if (existingSubmission.isPresent()) {
            return ResponseEntity.badRequest().body("Student has already submitted this quiz");
        }

        MCQSubmission quizSubmission = new MCQSubmission();
        quizSubmission.setStudent(student);
        quizSubmission.setQuiz(quiz);
        quizSubmission.setSubmissionTime(LocalDateTime.now());
        quizSubmission.setCheatingStatus(mcqQuizSubmissionDTO.getCheatingStatus());

        int totalPossiblePoints = 0;
        int totalPointsEarned = 0;

        for (StudentMCQAnswerDTO answerDTO : mcqQuizSubmissionDTO.getAnswers()) {
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            if (!question.getQuiz().getQuizId().equals(mcqQuizSubmissionDTO.getQuizId())) {
                return ResponseEntity.badRequest().body("One or more questions do not belong to the specified quiz");
            }

            Answer answer = answerRepository.findById(answerDTO.getAnswerId())
                    .orElseThrow(() -> new RuntimeException("Answer not found"));

            if (!answer.getQuestion().getQuestionId().equals(question.getQuestionId())) {
                return ResponseEntity.badRequest().body("One or more answers do not belong to the specified question");
            }
            boolean isCorrect = answer.isCorrect();
            int answerGrade = 0;
            totalPossiblePoints += question.getPoints();
            if (isCorrect) {
                totalPointsEarned += question.getPoints();
                answerGrade = question.getPoints();
            }

            StudentMCQAnswer studentAnswer = new StudentMCQAnswer();
            studentAnswer.setQuestion(question);
            studentAnswer.setAnswer(answer);
            studentAnswer.setCorrect(isCorrect);
            studentAnswer.setMcqSubmission(quizSubmission);
            studentAnswer.setGrade(answerGrade);
            quizSubmission.getAnswers().add(studentAnswer);
        }

        int totalGrade = quiz.getTotalGrade();
        double studentGrade = (double) totalPointsEarned / totalPossiblePoints * totalGrade;
        quizSubmission.setTotalGrade(studentGrade);

        MCQSubmission savedSubmission = quizSubmissionRepository.save(quizSubmission);

        CheatingReport cheatingReport = new CheatingReport();
        cheatingReport.setQuizSubmission(savedSubmission);
        cheatingReport.setFolderName();
        cheatingReportRepo.save(cheatingReport);
        savedSubmission.setCheatingReport(cheatingReport);
        quizSubmissionRepository.save(quizSubmission);
        return ResponseEntity.ok("Quiz submission saved successfully");
    }
    public MCQSubmission getQuizSubmission(Long submissionId) {
        return quizSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }
}
