package com.edu.eduplatform.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class QuizForStudentDTO {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalGrade;
    private long courseId;
    private List<QuestionForStudentDTO> questions;
}