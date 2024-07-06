package com.edu.eduplatform.dtos;

import com.edu.eduplatform.models.QuizSubmission;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PastQuizSubmissionStudent {
    private Long courseId;
    private Long quizId;
    private String courseName;
    private String quizTitle;
    private QuizSubmission quizSubmission;
}
