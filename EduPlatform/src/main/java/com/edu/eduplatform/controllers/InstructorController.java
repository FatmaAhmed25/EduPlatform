package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.AnnouncementDto;
import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.dtos.UpdateCourseDTO;
import com.edu.eduplatform.dtos.UpdateInstructorDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.services.CourseService;
import com.edu.eduplatform.services.InstructorService;
import com.google.api.Authentication;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructor")

@PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
public class InstructorController {

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private CourseService courseService;
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

//    @PostMapping("/courses/{instructorId}")
//    @SecurityRequirement(name="BearerAuth")
//    public ResponseEntity<String> createCourse(
//            @PathVariable Long instructorId,
//            @RequestBody CourseDTO courseDTO) {
//        try {
//            instructorService.createCourse(instructorId, courseDTO);
//            return ResponseEntity.ok("Course created for instructor successfully.");
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PutMapping("updateProfile/{instructorId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<String> updateInstructorProfile(@PathVariable Long instructorId,
                                                          @RequestBody UpdateInstructorDTO updateInstructorDTO) {
        try {
            instructorService.updateInstructor(instructorId, updateInstructorDTO);
            return ResponseEntity.ok("Profile details updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("courses/updateCourseDetails/{courseId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<String> updateCourse(@PathVariable Long courseId,
                                               @RequestBody UpdateCourseDTO updateCourseDTO) {
        try {
            courseService.updateCourse(courseId, updateCourseDTO);
            return ResponseEntity.ok("Course details updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{courseId}/announcements")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> getAnnouncements(@PathVariable Long courseId) {
        List<Announcement> announcements = instructorService.getAnnouncementsByCourse(courseId);
        return ResponseEntity.ok(announcements);
    }
}
