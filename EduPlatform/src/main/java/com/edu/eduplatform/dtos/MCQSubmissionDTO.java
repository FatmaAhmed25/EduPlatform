package com.edu.eduplatform.dtos;

import lombok.Data;

import java.util.List;

@Data
public class MCQSubmissionDTO {
    private Long studentId;
    private Long quizId;
    private List<StudentMCQAnswerDTO> answers;
}

