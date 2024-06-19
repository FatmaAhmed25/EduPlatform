package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.services.CourseContentService;
import com.edu.eduplatform.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseContentService contentService;
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseContentService contentService,CourseService courseService) {
        this.contentService = contentService;
        this.courseService = courseService;
    }
    @PostMapping("/create")
    public ResponseEntity<CourseDTO> createCourse(
            @RequestParam String title,
            @RequestParam String description) {
        try {
            CourseDTO courseDTO = courseService.generateCourse(title, description);
            return new ResponseEntity<>(courseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{courseId}/content")
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
            @RequestParam String fileName) {
        URL url = contentService.getFileUrl(courseId, fileName);
        return url != null ? new ResponseEntity<>(url, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{courseId}/content")
    public ResponseEntity<Void> deleteContent(
            @PathVariable String courseId,
            @RequestParam String fileName) {
        boolean deleted = contentService.deleteFile(courseId, fileName);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
