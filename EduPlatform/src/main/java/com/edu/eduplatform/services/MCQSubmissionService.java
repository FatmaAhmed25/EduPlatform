package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.MCQSubmissionDTO;

import com.edu.eduplatform.dtos.StudentMCQAnswerDTO;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public MCQSubmission submitQuiz( MCQSubmissionDTO mcqQuizSubmissionDTO) {
        Student student = studentRepository.findById(mcqQuizSubmissionDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Quiz quiz = quizRepository.findById(mcqQuizSubmissionDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        MCQSubmission quizSubmission = new MCQSubmission();
        quizSubmission.setStudent(student);
        quizSubmission.setQuiz(quiz);
        quizSubmission.setSubmissionTime(LocalDateTime.now());

        int totalPossiblePoints = 0;
        int totalPointsEarned = 0;

        for (StudentMCQAnswerDTO answerDTO : mcqQuizSubmissionDTO.getAnswers())
        {
            Question question = questionRepository.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            Answer answer = answerRepository.findById(answerDTO.getAnswerId())
                    .orElseThrow(() -> new RuntimeException("Answer not found"));

            boolean isCorrect = answer.isCorrect();
            int answerGrade=0;
            totalPossiblePoints += question.getPoints();
            if (isCorrect) {
                totalPointsEarned += question.getPoints();
                answerGrade=question.getPoints();
            }

            StudentMCQAnswer studentAnswer = new StudentMCQAnswer();
            studentAnswer.setQuestion(question);
            studentAnswer.setAnswer(answer);
            studentAnswer.setCorrect(isCorrect);
            studentAnswer.setMcqSubmission(quizSubmission);
            studentAnswer.setGrade(answerGrade);
            quizSubmission.getAnswers().add(studentAnswer);
        }

        // for calculating the student's grade
        int totalGrade = quiz.getTotalGrade();
        double studentGrade = (double) totalPointsEarned / totalPossiblePoints * totalGrade;

        quizSubmission.setTotalGrade(studentGrade);

        return quizSubmissionRepository.save(quizSubmission);
    }
    public MCQSubmission getQuizSubmission(Long submissionId) {
        return quizSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }
}
