package com.edu.eduplatform.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class GenerateMcqQuizDTO {

    private Long courseId;
    private String quizTitle;
    private String startTime;
    private String endTime;
    private int numOfQuestions;
    private List<MultipartFile> pdfFiles;
}
