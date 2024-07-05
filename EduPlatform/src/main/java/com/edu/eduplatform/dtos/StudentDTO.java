package com.edu.eduplatform.dtos;
import lombok.Data;

@Data
public class StudentDTO {
    private Long userID;
    private String username;
    private String email;
    private String userType;
}
