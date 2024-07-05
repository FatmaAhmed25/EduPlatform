package com.edu.eduplatform.dtos;

import com.edu.eduplatform.models.QuestionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class QuestionForStudentDTO {
    private Long questionId;
    private String text;
    private int points;
    private QuestionType questionType;
    private List<AnswerForStudentDTO> answers = new ArrayList<>();
}