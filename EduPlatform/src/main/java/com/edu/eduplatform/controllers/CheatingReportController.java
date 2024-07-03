package com.edu.eduplatform.controllers;

import com.edu.eduplatform.services.CheatingReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/cheating-report")
public class CheatingReportController {

    @Autowired
    private CheatingReportService cheatingReportService;

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping(value = "/save-photo", consumes = {"multipart/form-data"})
    public ResponseEntity<String> savePhoto(@RequestParam("studentId") Long studentId,
                                            @RequestParam("quizId") Long quizId,
                                            @RequestParam("file") MultipartFile file) {
        try {
            cheatingReportService.savePhoto(studentId, quizId, file);
            return ResponseEntity.ok("Photo saved successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save photo");
        }
    }
}
