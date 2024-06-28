package com.edu.eduplatform.dtos;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String userName;

}
