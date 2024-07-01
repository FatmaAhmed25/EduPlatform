package com.edu.eduplatform.dtos;


import lombok.Data;

@Data
public class QuestionAnswerDTO {
    private Long questionId;
    private String questionText;
    private String userAnswer;


}
