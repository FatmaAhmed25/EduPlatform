package com.edu.eduplatform.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetStudentQuizzesResponse {
    private Long quizId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalGrade;
    private boolean canTakeQuiz;
}
