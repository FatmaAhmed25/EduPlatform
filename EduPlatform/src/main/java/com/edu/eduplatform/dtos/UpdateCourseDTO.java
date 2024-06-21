package com.edu.eduplatform.dtos;

import lombok.Data;

@Data
public class UpdateCourseDTO {
    private String title;
    private String description;
    private String password;
}
