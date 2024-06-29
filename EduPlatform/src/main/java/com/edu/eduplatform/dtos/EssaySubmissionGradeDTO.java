package com.edu.eduplatform.dtos;

import lombok.Data;
import java.util.List;

@Data
public class EssaySubmissionGradeDTO {
    private Long quizId;
    private Long studentId;
    private List<StudentAnswerGradeDTO> grades;

    @Data
    public static class StudentAnswerGradeDTO {
        private Long answerId;
        private String grade;
    }
}
