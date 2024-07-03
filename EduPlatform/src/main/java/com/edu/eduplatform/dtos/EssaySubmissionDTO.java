package com.edu.eduplatform.dtos;

import com.edu.eduplatform.models.QuizSubmission;
import lombok.Data;


import java.util.List;

@Data
public class EssaySubmissionDTO {

    private Long studentId;
    private Long quizId;
    private List<StudentAnswerDTO> answers;
    private QuizSubmission.CheatingStatus cheatingStatus = QuizSubmission.CheatingStatus.PASSED;


}
