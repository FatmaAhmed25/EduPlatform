package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.dtos.AssignmentResponseDTO;
import com.edu.eduplatform.dtos.CreateCommentDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Comment;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.MaterialType;
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




    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT')")
    @GetMapping("/get-announcement/{announcementId}")
    public ResponseEntity<Object> getAnnouncementById(@PathVariable Long announcementId) {
        Object announcementObject = announcementService.getAnnouncementById(announcementId);

        if (announcementObject != null) {
            return ResponseEntity.ok(announcementObject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    @GetMapping("/{courseId}/lectures")
    public ResponseEntity<List<Announcement>> getLectureAnnouncements(@PathVariable Long courseId) {
        try {
            List<Announcement> announcements = announcementService.getLectureAnnouncements(courseId);
            return ResponseEntity.ok(announcements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    @GetMapping("/{courseId}/labs")
    public ResponseEntity<List<Announcement>> getLabAnnouncements(@PathVariable Long courseId) {
        try {
            List<Announcement> announcements = announcementService.getLabAnnouncements(courseId);
            return ResponseEntity.ok(announcements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    @GetMapping("/{courseId}/assignments")
    public ResponseEntity<List<AssignmentResponseDTO>> getAssignmentAnnouncements(@PathVariable Long courseId) {
        try {
            List<AssignmentResponseDTO> announcements = announcementService.getAssignmentAnnouncements(courseId);
            return ResponseEntity.ok(announcements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/get-student-notifications/{studentId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<Announcement>> getAnnouncementsForStudent(
            @PathVariable Long studentId
    ) {
        try {
            List<Announcement> announcements = announcementService.getNotiicationsForStudent(studentId);
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
            @RequestParam MaterialType materialType,
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam String content)
    {
        try {

            if (materialType == MaterialType.ASSIGNMENTS) {
                return ResponseEntity.badRequest().body("Invalid folder name");
            }
            AnnouncementDTO announcementDto=new AnnouncementDTO(title,content);
            announcementService.uploadMaterialAndNotifyStudents(instructorId, courseId, materialType, file, announcementDto);
            return ResponseEntity.ok("Material uploaded and students notified successfully");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid folder name");
        }
        catch (IOException e) {
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

    @PostMapping("/add-comment")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    public ResponseEntity<?> addCommentToAnnouncement(
            @RequestBody CreateCommentDTO createCommentDTO
    ) {
        try {
            Comment comment = announcementService.addComment(createCommentDTO);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add comment: " + e.getMessage());
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    @GetMapping("/getComments/{announcementId}")
    public ResponseEntity<List<Comment>> getAllCommentsForAnnouncement(@PathVariable Long announcementId) {
        List<Comment> comments = announcementService.getCommentsForAnnouncement(announcementId);
        return ResponseEntity.ok(comments);
    }


}
