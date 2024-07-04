package com.edu.eduplatform.dtos;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class getCommentDTO
{
    long commentId;
    UserCommentDTO userCommentDTO;
    String content;
    LocalDateTime createdAt;
}
