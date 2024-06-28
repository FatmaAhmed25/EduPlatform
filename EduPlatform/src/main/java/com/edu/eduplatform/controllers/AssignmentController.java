package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.dtos.AssignmentDTO;
import com.edu.eduplatform.dtos.AssignmentSubmissionDTO;
import com.edu.eduplatform.models.Assignment;
import com.edu.eduplatform.models.AssignmentSubmission;
import com.edu.eduplatform.services.AssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping(value="/{courseId}/create" ,consumes = {"multipart/form-data"})
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<Assignment> createAssignment(
            @PathVariable Long courseId,
            @RequestParam Long instructorId,
            @RequestParam MultipartFile file,
            @RequestParam LocalDateTime dueDate,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam boolean allowLateSubmissions) throws IOException {
        AnnouncementDTO announcementDto = new AnnouncementDTO(title, content);

        Assignment assignment = assignmentService.createAssignment(instructorId, courseId, file, announcementDto, dueDate, allowLateSubmissions);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @PostMapping(value="/submit", consumes = {"multipart/form-data"})
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<?> submitAssignment(
            @RequestParam Long studentId,
            @RequestParam Long assignmentId,
            @RequestParam MultipartFile file) throws IOException {
        try {
            assignmentService.submitAssignment(studentId, assignmentId, file);
            return ResponseEntity.ok("Assignment submitted successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @GetMapping("/{studentId}/assignments")
    public List<AssignmentDTO> getAssignmentsForStudent(@PathVariable Long studentId) {
        return assignmentService.getAssignmentsForStudent(studentId);
    }

    @GetMapping("/submissions/{assignmentId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<List<AssignmentSubmissionDTO>> getSubmissionsForAssignment(@PathVariable Long assignmentId) {
        List<AssignmentSubmissionDTO> submissions = assignmentService.getSubmissionsForAssignment(assignmentId);
        return ResponseEntity.ok().body(submissions);
    }


    @GetMapping("/submissions/student/{studentId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<List<AssignmentSubmissionDTO>> getSubmissionsForStudent(@PathVariable Long studentId) {
        List<AssignmentSubmissionDTO> submissions = assignmentService.getSubmissionsForStudent(studentId);
        return ResponseEntity.ok().body(submissions);
    }


}