package com.edu.eduplatform.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class AssignmentDTO {

    private String title;
    private String description;
    private LocalDateTime dueDate;
    String fileName;
    public AssignmentDTO(String title, String content) {
        this.title = title;
        this.description = content;
    }


}