package com.edu.eduplatform.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDTO {
    private String text;
    private int points;
    private List<AnswerDTO> answers = new ArrayList<>();
}