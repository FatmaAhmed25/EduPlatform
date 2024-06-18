package com.edu.eduplatform.dtos;

import com.edu.eduplatform.models.User;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private long userID;
    private String username;
    private String email;
    private String bio;
    private User.UserType userType;
    private String token;
}
