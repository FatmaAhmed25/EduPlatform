package com.edu.eduplatform.services;


import com.edu.eduplatform.dtos.EssaySubmissionDTO;
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
        essaySubmission.setSubmittedAt(LocalDateTime.now()); // Set submission timestamp

        List<StudentAnswer> studentAnswers = new ArrayList<>();
        for (StudentAnswerDTO studentAnswerDTO : essaySubmissionDTO.getAnswers()) {
            StudentAnswer studentAnswer = createStudentAnswer(studentAnswerDTO, essaySubmission);
            studentAnswers.add(studentAnswer);
        }
        essaySubmission.setAnswers(studentAnswers);

        // Save quiz submission
        essaySubmissionRepository.save(essaySubmission);
        return ResponseEntity.ok("Quiz submission saved successfully");
    }

    private StudentAnswer createStudentAnswer(StudentAnswerDTO studentAnswerDTO, EssaySubmission essaySubmission) {
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setAnswer(studentAnswerDTO.getAnswer());
        studentAnswer.setEssaySubmission(essaySubmission);
        return studentAnswer;
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

    public void setOverAllGrade(Long quizId, Long studentId,String grade)
    {
        EssaySubmission essaySubmission = essaySubmissionRepository.findByQuizQuizIdAndStudentUserID(quizId, studentId);
        if (essaySubmission != null) {
           essaySubmission.setOverallGrade(grade);
           essaySubmissionRepository.save(essaySubmission);
        }

    }

    public List<Long> getStudentAnswerIdsByQuizAndStudent(Long quizId, Long studentId) {
        EssaySubmission essaySubmission = essaySubmissionRepository.findByQuizQuizIdAndStudentUserID(quizId, studentId);
        if (essaySubmission != null) {
            return studentAnswerRepo.findByEssaySubmissionId(essaySubmission.getId()).stream()
                    .map(answer -> answer.getId())
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public void updateAnswerGrade(Long answerId, String grade) {
        StudentAnswer studentAnswer = studentAnswerRepo.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid answer ID: " + answerId));
        studentAnswer.setGrade(grade);
        studentAnswerRepo.save(studentAnswer);
    }
}
