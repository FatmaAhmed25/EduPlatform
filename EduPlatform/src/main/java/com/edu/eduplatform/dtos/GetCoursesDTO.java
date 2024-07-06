package com.edu.eduplatform.dtos;


import lombok.Data;

import java.util.List;

@Data
public class GetCoursesDTO {

    private Long id;
    private String title;
    private int numberOfEnrolledStudents;
    private String createdByInstructorName;
    private List<String> tasInstructorNames;
    private String courseCode;
    private String password;


}
