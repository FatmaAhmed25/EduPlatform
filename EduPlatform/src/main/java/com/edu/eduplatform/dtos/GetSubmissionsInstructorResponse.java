package com.edu.eduplatform.dtos;

import com.edu.eduplatform.models.QuizSubmission;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class GetSubmissionsInstructorResponse {

    private Long studentId;
    private Long courseId;
    private Long quizId;
    private String studentName;
    private QuizSubmission quizSubmission;

}