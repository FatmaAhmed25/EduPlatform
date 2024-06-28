package com.edu.eduplatform.dtos;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class getAnnouncementDTO
{

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String fileName;
    private List<CommentDTO> comments;
}
