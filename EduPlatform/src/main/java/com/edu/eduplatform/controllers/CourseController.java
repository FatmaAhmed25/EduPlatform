package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.services.CourseContentService;
import com.edu.eduplatform.services.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/courses")
public class CourseController {


    @Autowired
    private CourseContentService contentService;

    @Autowired
    private CourseService courseService;



    @PostMapping("/create/{instructorId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createCourse(
            @PathVariable Long instructorId,
            @RequestBody CourseDTO courseDTO){
        try {
            courseService.generateCourse(instructorId, courseDTO);
            return ResponseEntity.ok("Course "+courseDTO.getTitle()+ " created for instructor"+instructorId+" successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping(value ="/{courseId}/content",consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadContent(
            @PathVariable String courseId,
            @RequestParam String folderName,
            @RequestParam MultipartFile file) {
        try {
            String fileName = contentService.uploadFile(courseId, folderName, file);
            return new ResponseEntity<>(fileName, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{courseId}/content")
    public ResponseEntity<URL> getContentUrl(
            @PathVariable String courseId,
            @RequestParam String fileName) throws IOException {
        URL url = contentService.getFileUrl(courseId, fileName);
        return url != null ? new ResponseEntity<>(url, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{courseId}/content")
    public ResponseEntity<Void> deleteContent(
            @PathVariable String courseId,
            @RequestParam String fileName) throws IOException {
        boolean deleted = contentService.deleteFile(courseId, fileName);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
