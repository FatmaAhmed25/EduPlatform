package com.edu.eduplatform.dtos;

import com.edu.eduplatform.models.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class AssignmentResponseDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String fileName;
    private LocalDateTime dueDate;
    private boolean allowLateSubmissions;
    private List<CommentDTO> comments;

}
