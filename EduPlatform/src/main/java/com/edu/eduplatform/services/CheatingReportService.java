package com.edu.eduplatform.services;

import com.edu.eduplatform.models.Quiz;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.CheatingReportRepo;
import com.edu.eduplatform.repos.QuizRepository;
import com.edu.eduplatform.repos.QuizSubmissionRepo;
import com.edu.eduplatform.repos.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class CheatingReportService {

    @Autowired
    private CourseContentService courseContentService;

    @Autowired
    private QuizRepository quizRepository;


    public void savePhoto(Long studentId, Long quizId, MultipartFile multipartFile) throws IOException {

        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isEmpty()) {
            throw new IllegalArgumentException("Quiz with ID " + quizId + " not found");
        }

        Quiz quiz = quizOpt.get();
        Long courseId = quiz.getCourse().getCourseId();

        String fileName = "submission-reports"
                +"/quizId-"+quizId + "/"
                +"studentId-"+ studentId;

        courseContentService.uploadFile(String.valueOf(courseId),fileName,multipartFile);

    }


}
