package com.edu.eduplatform.dtos;

import com.edu.eduplatform.models.QuestionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuestionDTO {
    private String text;
    private int points;
    private QuestionType questionType;
    private List<AnswerDTO> answers = new ArrayList<>();

}