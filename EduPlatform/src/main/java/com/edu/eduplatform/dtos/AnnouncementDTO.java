package com.edu.eduplatform.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AnnouncementDTO {

    String title;
    String content;
    String fileName;

    public AnnouncementDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
