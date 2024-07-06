package com.edu.eduplatform.dtos;

import lombok.Data;

@Data
public class UserDTO {
    private long userId;
    private String username;
    private String email;
    private String password;
    private String bio;
    private String userType;
}