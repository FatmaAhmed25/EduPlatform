package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.*;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.AnswerRepository;
import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.repos.QuestionRepository;
import com.edu.eduplatform.repos.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${model.flask.url}")
    private String flaskBaseUrl;

    public Quiz createQuiz(QuizDTO quizDTO) {
        Course course = courseRepository.findById(quizDTO.getCourseId()).orElseThrow(() -> new RuntimeException("Course not found"));

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setStartTime(quizDTO.getStartTime());
        quiz.setEndTime(quizDTO.getEndTime());
        quiz.setTotalGrade(quizDTO.getTotalGrade());
        quiz.setCourse(course);

        quiz = quizRepository.save(quiz);

        for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
            Question question;

            if (questionDTO.getQuestionType().equals(QuestionType.MCQ)) {
                question = new MCQQuestion();
                for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
                    Answer answer = new Answer();
                    answer.setText(answerDTO.getText());
                    answer.setCorrect(answerDTO.isCorrect());
                    ((MCQQuestion) question).getAnswers().add(answer);
                    answer.setQuestion((MCQQuestion) question);
                }
            } else if (questionDTO.getQuestionType().equals(QuestionType.ESSAY)) {
                question = new EssayQuestion();
            } else {
                throw new RuntimeException("Invalid question type");
            }

            question.setText(questionDTO.getText());
            question.setPoints(questionDTO.getPoints());
            question.setQuiz(quiz);
            questionRepository.save(question);
        }
        return quiz;
    }

    public Question addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        question.setQuiz(quiz);
        return questionRepository.save(question);
    }

    public Answer addAnswerToQuestion(Long questionId, Answer answer) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question not found"));
        if (question instanceof MCQQuestion) {
            answer.setQuestion((MCQQuestion) question);
            return answerRepository.save(answer);
        } else {
            throw new RuntimeException("Answers can only be added to MCQ questions");
        }
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findByQuizIdWithQuestions(quizId);
    }

    public List<Quiz> getQuizzesByCourseId(Long courseId) {
        return quizRepository.findByCourseCourseId(courseId);
    }

    public List<String> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findTextByQuizQuizId(quizId);
    }

    public Quiz getQuizByIdForInstructor(Long quizId) {
        return quizRepository.findByQuizIdWithQuestions(quizId);
    }

    public QuizForStudentDTO getQuizForStudentById(Long quizId) {
        Quiz quiz = quizRepository.findByQuizIdWithQuestions(quizId);
        return modelMapper.map(quiz, QuizForStudentDTO.class);
    }

    public Quiz generateAndCreateMcqQuiz(GenerateMcqQuizDTO requestDTO) {
        String flaskUrl = flaskBaseUrl + "/mcq";

        // Prepare headers and request body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("courseId", requestDTO.getCourseId());
        body.add("quiz_title", requestDTO.getQuizTitle());
        body.add("startTime", requestDTO.getStartTime());
        body.add("endTime", requestDTO.getEndTime());
        body.add("numOfQuestions", requestDTO.getNumOfQuestions());
        for (MultipartFile file : requestDTO.getPdfFiles()) {
            body.add("pdf_files", file.getResource());
        }

        // Create the request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Configure ObjectMapper with JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Send the request to Flask
        ResponseEntity<String> responseEntity = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, String.class);

        // Process the response as needed
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to generate quiz");
        }

        // Deserialize the response body into QuizDTO
        QuizDTO quizDTO;
        try {
            quizDTO = objectMapper.readValue(responseEntity.getBody(), QuizDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing response", e);
        }

        // Convert QuizDTO to Quiz entity and save the quiz
        return createQuiz(quizDTO);
    }
}