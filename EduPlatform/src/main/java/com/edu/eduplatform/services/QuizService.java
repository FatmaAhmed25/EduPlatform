package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.*;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private StudentMCQAnswerRepo studentMCQAnswerRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private InstructorRepo instructorRepo;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${model.flask.url}")
    private String flaskBaseUrl;

    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private QuizSubmissionRepo quizSubmissionRepository;

    @Autowired
    private CourseContentService courseContentService;

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
                question.setQuestionType(QuestionType.MCQ);
                for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
                    Answer answer = new Answer();
                    answer.setText(answerDTO.getText());
                    answer.setCorrect(answerDTO.isCorrect());
                    ((MCQQuestion) question).getAnswers().add(answer);
                    answer.setQuestion((MCQQuestion) question);
                }
            } else if (questionDTO.getQuestionType().equals(QuestionType.ESSAY)) {
                question = new EssayQuestion();
                question.setQuestionType(QuestionType.ESSAY);
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

    public Quiz updateQuiz(Long quizId, Long instructorId, QuizDTO quizDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Course course = quiz.getCourse();
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        if (!course.getCreatedBy().equals(instructor) && !course.getTaInstructors().contains(instructor)) {
            throw new RuntimeException("Instructor does not have permission to update this quiz");
        }

        quiz.setTitle(quizDTO.getTitle());
        quiz.setStartTime(quizDTO.getStartTime());
        quiz.setEndTime(quizDTO.getEndTime());
        quiz.setTotalGrade(quizDTO.getTotalGrade());

        // Clear existing questions and answers
        for (Question question : quiz.getQuestions()) {
            if (question instanceof MCQQuestion) {
                MCQQuestion mcqQuestion = (MCQQuestion) question;

                // Delete related StudentMCQAnswers
                for (Answer answer : mcqQuestion.getAnswers()) {
                    List<StudentMCQAnswer> studentMCQAnswers = studentMCQAnswerRepo.findByAnswer(answer);
                    studentMCQAnswerRepo.deleteAll(studentMCQAnswers);
                }

                answerRepository.deleteAll(mcqQuestion.getAnswers());
            }

            List<StudentMCQAnswer> studentMCQAnswers = studentMCQAnswerRepo.findByQuestion(question);
            studentMCQAnswerRepo.deleteAll(studentMCQAnswers);

            questionRepository.delete(question);
        }
        quiz.getQuestions().clear();

        // Add new questions and answers
        for (QuestionDTO questionDTO : quizDTO.getQuestions()) {
            Question question;

            if (questionDTO.getQuestionType().equals(QuestionType.MCQ)) {
                question = new MCQQuestion();
                question.setQuestionType(QuestionType.MCQ);
                for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
                    Answer answer = new Answer();
                    answer.setText(answerDTO.getText());
                    answer.setCorrect(answerDTO.isCorrect());
                    ((MCQQuestion) question).getAnswers().add(answer);
                    answer.setQuestion((MCQQuestion) question);
                }
            } else if (questionDTO.getQuestionType().equals(QuestionType.ESSAY)) {
                question = new EssayQuestion();
                question.setQuestionType(QuestionType.ESSAY);
            } else {
                throw new RuntimeException("Invalid question type");
            }

            question.setText(questionDTO.getText());
            question.setPoints(questionDTO.getPoints());
            question.setQuiz(quiz);
            quiz.getQuestions().add(question);
            questionRepository.save(question);
        }

        return quizRepository.save(quiz);
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

    public List<String> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findTextByQuizQuizId(quizId);
    }

    public Quiz getQuizByIdForInstructor(Long quizId) {
        return quizRepository.findByQuizIdWithQuestions(quizId);
    }

    public QuizForStudentDTO getQuizForStudentById(Long studentId, Long quizId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        boolean alreadySubmitted = quizSubmissionRepository.existsByQuizAndStudent(quiz, student);
        if (alreadySubmitted) {
            throw new EntityNotFoundException("Student has already submitted this quiz");
        }

        return modelMapper.map(quiz, QuizForStudentDTO.class);
    }

    public Quiz generateQuiz(GenerateQuizDTO requestDTO,String url) throws IOException {
        String flaskUrl = flaskBaseUrl + url;
        System.out.println(flaskUrl);
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
        Quiz quiz=createQuiz(quizDTO);

        List<Question> questions = questionRepository.findAnyQuestionByQuizId(quiz.getQuizId());
        boolean hasEssayQuestion = questions.stream().anyMatch(question -> question.getQuestionType() == QuestionType.ESSAY);

        System.out.println(hasEssayQuestion);
        if(hasEssayQuestion)
        {
            for (MultipartFile file : requestDTO.getPdfFiles()) {
                courseContentService.uploadFile(requestDTO.getCourseId().toString(), "creation-pdfs/" + "quizId-" + quiz.getQuizId(), file);
            }
        }


        return quiz;
    }

    public Quiz generateAndCreateMcqQuiz(GenerateQuizDTO requestDTO) throws IOException {
       return generateQuiz(requestDTO,"/mcq");

    }

    public Quiz generateEssayQuiz(GenerateQuizDTO requestDTO) throws IOException {
        return generateQuiz(requestDTO,"/essay");

    }
    public boolean isQuizExists(Long quizId) {
        if (!quizRepository.existsByQuizId(quizId)) {
            throw new EntityNotFoundException("Quiz with id " + quizId + " not found");
        }
        return true;
    }

    public List<GetStudentQuizzesResponse> getQuizzesForStudent(Long studentId) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        return quizRepository.findByCourse_Students_UserID(studentId).stream()
                .filter(quiz -> quiz.getEndTime().isAfter(now))
                .map(quiz -> new GetStudentQuizzesResponse(
                        quiz.getCourse().getCourseId(),
                        quiz.getQuizId(),
                        quiz.getTitle(),
                        quiz.getStartTime(),
                        quiz.getEndTime(),
                        quiz.getTotalGrade(),
                        quiz.getStartTime().isBefore(now)))
                .collect(Collectors.toList());
    }


    public List<GetCourseQuizzesResponse> getQuizzesForCourse(Long courseId) {
        return quizRepository.findByCourse_CourseId(courseId).stream()
                .map(quiz -> new GetCourseQuizzesResponse(
                        quiz.getQuizId(),
                        quiz.getTitle(),
                        quiz.getStartTime(),
                        quiz.getEndTime(),
                        quiz.getTotalGrade()))
                .collect(Collectors.toList());
    }



    public List<Quiz> getQuizzesWithNullEssaySubmissions(Long courseId) {
        return quizRepository.findQuizzesWithNullEssaySubmissions(courseId);
    }

}