package com.edu.eduplatform.services;


import com.edu.eduplatform.dtos.EssaySubmissionDTO;
import com.edu.eduplatform.dtos.QuestionAnswerDTO;
import com.edu.eduplatform.dtos.StudentAnswerDTO;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EssaySubmissionService
{
    @Autowired
    private EssaySubmissionRepo essaySubmissionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private StudentAnswerRepo studentAnswerRepo;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ModelMapper modelMapper;

    public  ResponseEntity<String>  saveQuizSubmission(EssaySubmissionDTO essaySubmissionDTO) {
        Quiz quiz = quizRepository.findById(essaySubmissionDTO.getQuizId())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
        Student student = studentRepo.findById(essaySubmissionDTO.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Optional<EssaySubmission> existingSubmission = essaySubmissionRepository.findByQuizAndStudent(quiz, student);
        if (existingSubmission.isPresent()) {
            return ResponseEntity.badRequest().body("Student has already submitted this quiz");
        }
        EssaySubmission essaySubmission = new EssaySubmission();
        essaySubmission.setQuiz(quiz);
        essaySubmission.setStudent(student);
        essaySubmission.setSubmissionTime(LocalDateTime.now()); // Set submission timestamp

        List<StudentEssayAnswer> studentEssayAnswers = new ArrayList<>();
        for (StudentAnswerDTO studentAnswerDTO : essaySubmissionDTO.getAnswers()) {
            StudentEssayAnswer studentEssayAnswer = createStudentAnswer(studentAnswerDTO, essaySubmission);
            studentEssayAnswers.add(studentEssayAnswer);
        }
        essaySubmission.setAnswers(studentEssayAnswers);

        // Save quiz submission
        essaySubmissionRepository.save(essaySubmission);
        return ResponseEntity.ok("Quiz submission saved successfully");
    }

    private StudentEssayAnswer createStudentAnswer(StudentAnswerDTO studentAnswerDTO, EssaySubmission essaySubmission) {
        Question question = questionRepository.findById(studentAnswerDTO.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        StudentEssayAnswer studentEssayAnswer = new StudentEssayAnswer();
        studentEssayAnswer.setAnswer(studentAnswerDTO.getAnswer());
        studentEssayAnswer.setQuestion(question);
        studentEssayAnswer.setEssaySubmission(essaySubmission);
        return studentEssayAnswer;
    }


    public List<StudentAnswerDTO> getStudentAnswersByQuizAndStudent(Long quizId, Long studentId) {
        EssaySubmission essaySubmission = essaySubmissionRepository.findByQuizQuizIdAndStudentUserID(quizId, studentId);
        if (essaySubmission != null) {
            return studentAnswerRepo.findByEssaySubmissionId(essaySubmission.getId()).stream()
                    .map(answer -> modelMapper.map(answer, StudentAnswerDTO.class))
                    .collect(Collectors.toList());
        }
        return List.of();
    }





}
