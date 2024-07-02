package com.edu.eduplatform.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
public class GenerateQuizDTO {

    private Long courseId;
    private String quizTitle;
    private String startTime;
    private String endTime;
    private int numOfQuestions;
    private List<MultipartFile> pdfFiles;
}
