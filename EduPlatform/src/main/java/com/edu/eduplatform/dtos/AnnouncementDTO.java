package com.edu.eduplatform.dtos;


import com.edu.eduplatform.models.MaterialType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AnnouncementDTO {

    String title;
    String content;
    String fileName;
    MaterialType materialType;
    public AnnouncementDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
    public AnnouncementDTO() {
    }
}
