package com.edu.eduplatform.dtos;


import lombok.Data;

@Data
public class UpdateUserDTO {

    private Long userId;
    private String username;
    private String email;
    private String password;
    private String bio;
}
