package com.edu.eduplatform.dtos;

import lombok.Data;

@Data
public class CourseResponseDTO {
    private Long courseId;
    private String courseCode;
    private String title;
    private String description;
    private String password;
    

}
