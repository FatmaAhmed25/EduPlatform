package com.edu.eduplatform.controllers;

import com.edu.eduplatform.services.AutoGradeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
public class AutoGradeController {



    @Autowired
    AutoGradeService autoGradeService;
    @PostMapping(value="/autograde", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<String> essayAutoGrade(
            @RequestParam Long quizId,
            @RequestParam Long studentId,
            @RequestParam("pdfFiles") List<MultipartFile> pdfFiles
    )
    {
        try {
            autoGradeService.essayAutoGrade(quizId, studentId, pdfFiles);
            return ResponseEntity.ok("Essays graded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to grade essays: " + e.getMessage());
        }
    }


    @PostMapping(value="/grade-essays", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<String> gradeEssays(
            @RequestParam Long quizId,
            @RequestParam List<Long> studentIds,
            @RequestParam("pdfFiles")  List<MultipartFile> pdfFiles) {

        try {
            autoGradeService.essayAutoGradeAll(quizId, studentIds, pdfFiles);
            return ResponseEntity.ok("Essays graded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to grade essays: " + e.getMessage());
        }
    }

}
