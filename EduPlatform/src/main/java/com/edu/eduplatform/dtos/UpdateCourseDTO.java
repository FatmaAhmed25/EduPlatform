package com.edu.eduplatform.dtos;

import lombok.Data;

@Data
public class UpdateCourseDTO {
    private Long courseId;
    private String title;
    private String description;
    private String password;
}
