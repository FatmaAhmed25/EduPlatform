package com.edu.eduplatform.services;


import com.edu.eduplatform.dtos.QuestionAnswerDTO;
import com.edu.eduplatform.dtos.QuizDTO;
import com.edu.eduplatform.models.EssaySubmission;
import com.edu.eduplatform.models.Question;
import com.edu.eduplatform.models.StudentEssayAnswer;
import com.edu.eduplatform.repos.EssaySubmissionRepo;
import com.edu.eduplatform.repos.QuestionRepository;
import com.edu.eduplatform.repos.QuizRepository;
import com.edu.eduplatform.repos.StudentAnswerRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutoGradeService
{

    @Autowired
    private EssaySubmissionRepo essaySubmissionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;


    @Value("${model.flask.url}")
    private String flaskBaseUrl;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StudentAnswerRepo studentAnswerRepo;
    public void setOverAllGrade(Long quizId, Long studentId,double grade)
    {
        EssaySubmission essaySubmission = essaySubmissionRepository.findByQuizQuizIdAndStudentUserID(quizId, studentId);
        if (essaySubmission != null) {
            essaySubmission.setTotalGrade(grade);
            essaySubmissionRepository.save(essaySubmission);
        }

    }


    public List<QuestionAnswerDTO> getQuestionAnswersByQuizIdAndStudentId(Long quizId, Long studentId) {
        EssaySubmission essaySubmission = essaySubmissionRepository.findByQuizQuizIdAndStudentUserID(quizId, studentId);
        if (essaySubmission == null) {
            throw new IllegalArgumentException("Submission not found for the given quiz and student.");
        }

        List<StudentEssayAnswer> studentEssayAnswers = studentAnswerRepo.findByEssaySubmissionId(essaySubmission.getId());

        return studentEssayAnswers.stream().map(answer -> {
            Question question = answer.getQuestion();
            QuestionAnswerDTO dto = new QuestionAnswerDTO();
            dto.setQuestionId(question.getQuestionId());
            dto.setQuestionText(question.getText());
            dto.setUserAnswer(answer.getAnswer());
            return dto;
        }).collect(Collectors.toList());
    }

    public void updateAnswerGrade(Long answerId, double grade) {
        StudentEssayAnswer studentEssayAnswer = studentAnswerRepo.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid answer ID: " + answerId));
        studentEssayAnswer.setGrade(grade);
        System.out.println("here");
        studentAnswerRepo.save(studentEssayAnswer);
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

    public void essayAutoGrade(Long quizId, Long studentId, List<MultipartFile> pdfFiles) {
        // Fetch questions and answers from the database if needed
         List<QuestionAnswerDTO> questionAnswers = getQuestionAnswersByQuizIdAndStudentId(quizId, studentId);
         List<String> questions = questionAnswers.stream().map(QuestionAnswerDTO::getQuestionText).collect(Collectors.toList());
         List<String> userAnswers = questionAnswers.stream().map(QuestionAnswerDTO::getUserAnswer).collect(Collectors.toList());
         List<Long> questionsIds= questionAnswers.stream().map(QuestionAnswerDTO::getQuestionId).collect(Collectors.toList());


        System.out.println(questions);
        System.out.println(userAnswers);
        // Prepare request to Flask API
        String flaskUrl = flaskBaseUrl + "/grade_answers";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for (MultipartFile file : pdfFiles) {
            try {
                // Convert MultipartFile to ByteArrayResource
                ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename(); // Set original filename
                    }
                };
                body.add("pdf_files", resource);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read PDF file: " + file.getOriginalFilename(), e);
            }
        }

         body.add("questions", String.join("\n", questions));
         body.add("user_answers", String.join("\n", userAnswers));


        // Create the request entity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send the request to Flask API
        ResponseEntity<String> responseEntity = restTemplate.exchange(flaskUrl, HttpMethod.POST, requestEntity, String.class);

        // Process the response
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to grade essays using the Flask API");
        }

        // Parse JSON response for overallGrade and grades
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode responseJson = objectMapper.readTree(responseEntity.getBody());
//            double overallGrade = responseJson.path("overallGrade").asDouble();
            JsonNode gradesNode = responseJson.path("grades");
             List<Double> grades = objectMapper.convertValue(gradesNode, new TypeReference<List<Double>>() {});

//            System.out.println("Overall Grade: " + overallGrade);
            System.out.println("Grades:");
             for (double grade : grades) {
                 System.out.println(grade);
             }
            System.out.println(questionsIds);
             List<Long> answerIds = getStudentAnswerIdsByQuizAndStudent(quizId, studentId);
             double studentGrade=0;
             double totalPoints=0;
             int quizGrade=quizRepository.findById(quizId).get().getTotalGrade();
             for (int i = 0; i < questionsIds.size(); i++)
             {
                 System.out.println("grade"+grades.get(i));
                 double currentPoints=questionRepository.findById(questionsIds.get(i)).get().getPoints();
                 double currentGrade=grades.get(i)*currentPoints;
                 System.out.println("no of points  "+questionRepository.findById(questionsIds.get(i)).get().getPoints());
                 System.out.println("current"+currentGrade);
                 updateAnswerGrade(answerIds.get(i),currentGrade);
                 studentGrade+=currentGrade;
                 totalPoints+=currentPoints;
             }
             double actualGrade=studentGrade / totalPoints * quizGrade;
             setOverAllGrade(quizId, studentId, actualGrade);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse response: " + responseEntity.getBody(), e);
        }
    }
}



