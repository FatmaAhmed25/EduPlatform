package com.edu.eduplatform.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private String notificationMessage;
}
