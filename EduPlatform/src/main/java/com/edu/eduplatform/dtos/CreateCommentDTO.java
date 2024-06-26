package com.edu.eduplatform.dtos;

import lombok.Data;

@Data
public class CreateCommentDTO {
    private Long announcementId;
    private Long userId;
    private String commentContent;
}
