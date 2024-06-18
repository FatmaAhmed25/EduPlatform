package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.services.InstructorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructor")

@PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @GetMapping("/courses/{instructorId}")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<List<Course>> getCoursesByInstructorId(@PathVariable Long instructorId) {
        List<Course> courses = instructorService.getCoursesByInstructorId(instructorId);
        if (courses != null) {
            return ResponseEntity.ok(courses);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/courses/{instructorId}")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<String> createCourse(
            @PathVariable Long instructorId,
            @RequestBody CourseDTO courseDTO) {
        try {
            instructorService.createCourse(instructorId, courseDTO);
            return ResponseEntity.ok("Course created for instructor successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
