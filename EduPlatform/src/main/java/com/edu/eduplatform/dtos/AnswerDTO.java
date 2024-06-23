package com.edu.eduplatform.dtos;

import lombok.Data;

@Data
public class AnswerDTO {
    private String text;
    private boolean isCorrect;
}