package com.edu.eduplatform.controllers;

import com.edu.eduplatform.annotations.ValidateCourse;
import com.edu.eduplatform.annotations.ValidateInstructor;
import com.edu.eduplatform.annotations.ValidateStudent;
import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.dtos.CourseResponseDTO;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.services.CourseContentService;
import com.edu.eduplatform.services.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {


    @Autowired
    private CourseContentService contentService;

    @Autowired
    private CourseService courseService;


    @GetMapping("/search")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT','ROLE_ADMIN')")
    public List<Course> searchCourses(@RequestParam String searchTerm) {
        return courseService.searchCourses(searchTerm);
    }



    @GetMapping("/search/by-code")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT','ROLE_ADMIN')")
    public List<Course> findByCourseCode(@RequestParam String courseCode) {
        return courseService.findByCourseCode(courseCode);
    }

    @GetMapping("/search/by-title")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT','ROLE_ADMIN')")
    public List<Course> findByTitle(@RequestParam String title) {
        return courseService.findByTitle(title);
    }



    @PostMapping("/create/course/{instructorId}")
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

    @PostMapping("/{courseId}/enroll-student")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> enrollStudent(@PathVariable @ValidateCourse Long courseId, @RequestParam @ValidateStudent Long studentId, @RequestParam String password) {
        try {
            return courseService.enrollStudentInCourse(courseId, studentId, password);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/enroll-by-code")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> enrollStudentByCode(@RequestParam String courseCode, @RequestParam @ValidateStudent Long studentId, @RequestParam String password) {
        try {
            return courseService.enrollStudentInCourseByCode(courseCode, studentId, password);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }


    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/{instructorId}/get-courses/instructor")
    public ResponseEntity<?> getCoursesCreatedByInstructor(@PathVariable @ValidateInstructor Long instructorId) {
        try {
            List<CourseResponseDTO> courses = courseService.getCoursesCreatedByInstructor(instructorId);
            return ResponseEntity.ok(courses);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @PostMapping("/{courseId}/assign-ta/{taId}")
    public ResponseEntity<String> assignTAToCourse(@RequestParam @ValidateInstructor Long instructorId, @PathVariable @ValidateCourse Long courseId, @PathVariable @ValidateInstructor Long taId) throws Exception {
        try {
            courseService.assignTAToCourse(instructorId, courseId, taId);
            return ResponseEntity.ok("TA assigned to course successfully");
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @PostMapping(value ="/{courseId}/upload-content",consumes = {"multipart/form-data"})
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
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    @GetMapping("/{courseId}/get-content")
    public ResponseEntity<URL> getContentUrl(
            @PathVariable String courseId,
            @RequestParam String fileName) throws IOException {
        URL url = contentService.getFileUrl(courseId, fileName);
        return url != null ? new ResponseEntity<>(url, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    @DeleteMapping("/{courseId}/delete-content")
    public ResponseEntity<Void> deleteContent(
            @PathVariable String courseId,
            @RequestParam String fileName) throws IOException {
        boolean deleted = contentService.deleteFile(courseId, fileName);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
