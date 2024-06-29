package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.NotificationDTO;
import com.edu.eduplatform.services.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController
{

    @Autowired
    NotificationService notificationService;

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @SecurityRequirement(name="BearerAuth")
    @GetMapping("/get-notifications-history/{studentId}")
    public ResponseEntity <List<NotificationDTO>> getNotificationsForStudents(@PathVariable long studentId)
    {
        return ResponseEntity.ok(notificationService.getNotificationsForStudents(studentId));
    }
}
