package com.edu.eduplatform.dtos;

import com.edu.eduplatform.annotations.ValidateCourse;
import lombok.Data;

@Data
public class UpdateCourseDTO {
    private Long courseId;
    private String title;
    private String description;
    private String password;
}