package com.edu.eduplatform.controllers;


import com.edu.eduplatform.annotations.*;
import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.dtos.AssignmentResponseDTO;
import com.edu.eduplatform.dtos.CreateCommentDTO;
import com.edu.eduplatform.dtos.getCommentDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Comment;
import com.edu.eduplatform.models.MaterialType;
import com.edu.eduplatform.services.AnnouncementService;
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
import java.util.List;

@RestController
@RequestMapping("/announcment")
public class AnnouncementController {


    @Autowired
    CourseService courseService;

    @Autowired
    AnnouncementService announcementService;

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT','ROLE_INSTRUCTOR')")
    @GetMapping("/get-announcement/{announcementId}")
    public ResponseEntity<Object> getAnnouncementById(@PathVariable Long announcementId) {
        try {
            Object announcementObject = announcementService.getAnnouncementById(announcementId);


            if (announcementObject != null) {
                return ResponseEntity.ok(announcementObject);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
        catch(EntityNotFoundException ex)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/{instructorId}/{courseId}/lectures/instructor")
    @ValidateInstructorBelongsToCourse
    public ResponseEntity<List<Announcement>> getLectureAnnouncementsForInstructor(@PathVariable @ValidateInstructor Long instructorId, @PathVariable @ValidateCourse Long courseId) {
        try {
            List<Announcement> announcements = announcementService.getLectureAnnouncements(courseId);
            return ResponseEntity.ok(announcements);
        } catch(EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority( 'ROLE_STUDENT')")
    @GetMapping("/{studentId}/{courseId}/lectures/student")
    @ValidateStudentEnrollmentInCourse
    public ResponseEntity<List<Announcement>> getLectureAnnouncementsForStudent(@PathVariable @ValidateStudent Long studentId,@PathVariable @ValidateCourse Long courseId) {
        try {
            List<Announcement> announcements = announcementService.getLectureAnnouncements(courseId);
            return ResponseEntity.ok(announcements);
        } catch(EntityNotFoundException ex)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
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
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }


    @PostMapping("/{courseId}/{instructorId}/create-announcement")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> createAnnouncement(@PathVariable Long courseId,@PathVariable Long instructorId,
                                                @RequestBody AnnouncementDTO announcementDto) {
        Announcement announcement = announcementService.createAnnouncement(courseId, instructorId, announcementDto);
        return ResponseEntity.ok(announcement);
    }


    @PutMapping(value = "/{courseId}/{instructorId}/update-announcement/{announcementId}",consumes = {"multipart/form-data"})
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> updateAnnouncement(@PathVariable @ValidateCourse Long courseId,
                                                @PathVariable @ValidateInstructor Long instructorId,
                                                @PathVariable Long announcementId,
                                                @RequestParam(required = false) String title,
                                                @RequestParam(required = false) String content,
                                                @RequestParam(required = false) MaterialType materialType,
                                                @RequestParam(required = false) MultipartFile file) {
        try {
            if (materialType == MaterialType.ASSIGNMENTS) {
                return ResponseEntity.badRequest().body("Invalid folder name");
            }
            Announcement updatedAnnouncement = announcementService.updateAnnouncement(courseId, instructorId, announcementId, title, content, materialType, file);
            return ResponseEntity.ok(updatedAnnouncement);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to update material: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}/{instructorId}/{announcementId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable @ValidateCourse Long courseId,
                                                @PathVariable @ValidateInstructor Long instructorId,
                                                @PathVariable Long announcementId) {
        try {
            announcementService.deleteAnnouncement(courseId, instructorId, announcementId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.status(500).build();
        }
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
    public ResponseEntity<List<getCommentDTO>> getAllCommentsForAnnouncement(@PathVariable Long announcementId) {
        List<getCommentDTO> comments = announcementService.getCommentsForAnnouncement(announcementId);
        return ResponseEntity.ok(comments);
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/{instructorId}/{courseId}/videos/instructor")
    @ValidateInstructorBelongsToCourse
    public ResponseEntity<List<Announcement>> getVideoAnnouncementsForInstructor(@PathVariable @ValidateInstructor Long instructorId, @PathVariable @ValidateCourse Long courseId) {
        try {
            List<Announcement> announcements = announcementService.getVideoAnnouncements(courseId);
            return ResponseEntity.ok(announcements);
        } catch(EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/get-announcement/{InstructorId}/{announcementId}")
        public ResponseEntity<Object> getAnnouncementInstructorById(@PathVariable @ValidateInstructor Long InstructorId, @PathVariable Long announcementId) {
        try {
            Object announcementObject = announcementService.getAnnouncementDetailsById(announcementId);

            if (announcementObject != null) {
                return ResponseEntity.ok(announcementObject);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR')")
    @GetMapping("/announcement/{id}/is-assignment")
    public boolean isAssignment(@PathVariable("id") Long id)
    {
        return announcementService.isAssignment(id);
    }

}
