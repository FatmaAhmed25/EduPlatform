package com.edu.eduplatform.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class AnswerForStudentDTO {
    private Long answerId;

    private String text;

    @JsonIgnore
    private boolean isCorrect;
}
