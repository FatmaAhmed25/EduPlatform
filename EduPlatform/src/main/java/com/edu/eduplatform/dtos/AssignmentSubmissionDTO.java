package com.edu.eduplatform.dtos;


import lombok.Data;

@Data
public class AssignmentSubmissionDTO {
    private Long id;
    private String submissionText;
    private UserDTO student;
}