package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.services.AnnouncementService;
import com.edu.eduplatform.services.CourseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/announcment")
public class AnnouncementController {


    @Autowired
    CourseService courseService;

    @Autowired
    AnnouncementService announcementService;

    @GetMapping("/get-student-announcements/{studentId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<Announcement>> getAnnouncementsForStudent(
            @PathVariable Long studentId
    ) {
        try {
            List<Announcement> announcements = announcementService.getAnnouncementsForStudent(studentId);
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value="/{instructorId}/courses/{courseId}/upload-material" ,consumes = {"multipart/form-data"})
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> uploadMaterialAndNotifyStudents(
            @PathVariable Long instructorId,
            @PathVariable Long courseId,
            @RequestParam String folderName,
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam String content

    ) {
        try {
            AnnouncementDTO announcementDto=new AnnouncementDTO(title,content);
            announcementService.uploadMaterialAndNotifyStudents(instructorId, courseId, folderName, file, announcementDto);
            return ResponseEntity.ok("Material uploaded and students notified successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload material: " + e.getMessage());
        }
    }


    @PostMapping("/{courseId}/create-announcement")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createAnnouncement(@PathVariable Long courseId,
                                                @RequestBody AnnouncementDTO announcementDto) {
        Course course = courseService.getCourseById(courseId);

        Long instructorId = course.getCreatedBy().getUserID();
        Announcement announcement = announcementService.createAnnouncement(courseId, instructorId, announcementDto);
        return ResponseEntity.ok(announcement);
    }




}
