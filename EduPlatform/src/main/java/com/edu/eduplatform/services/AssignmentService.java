package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.dtos.AssignmentSubmissionDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Assignment;
import com.edu.eduplatform.models.AssignmentSubmission;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.AssignmentRepo;
import com.edu.eduplatform.repos.AssignmentSubmissionRepo;
import com.edu.eduplatform.repos.StudentRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AssignmentRepo assignmentRepo;

    @Autowired
    private AssignmentSubmissionRepo assignmentSubmissionRepo;

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private CourseContentService courseContentService;


    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public Assignment createAssignment(Long instructorId, Long courseId, MultipartFile file, AnnouncementDTO announcementDto, LocalDateTime dueDate) throws IOException {
        String folderName = "assignments/instructor/" + instructorId;
        Announcement announcement = announcementService.uploadMaterialAndNotifyStudents(instructorId, courseId, folderName, file, announcementDto);

        // Use ModelMapper to map Announcement to Assignment
        Assignment assignment = modelMapper.map(announcement, Assignment.class);
        assignment.setDueDate(dueDate);

        return assignmentRepo.save(assignment);
    }

    @Transactional
    public void submitAssignment(Long studentId, Long assignmentId, MultipartFile file) throws IOException {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String folderName = "assignments/" + "submissions/student"+studentId;
        String fileName = courseContentService.uploadFile(assignment.getCourse().getCourseId().toString(), folderName, file);

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setFileName(fileName);

        assignmentSubmissionRepo.save(submission);
    }


    public List<AssignmentSubmissionDTO> getSubmissionsForAssignment(Long assignmentId) {
        List<AssignmentSubmission> submissions = assignmentSubmissionRepo.findByAssignmentId(assignmentId);
        return mapToDTO(submissions);
    }

    public List<AssignmentSubmissionDTO> getSubmissionsForStudent(Long studentId) {
        List<AssignmentSubmission> submissions = assignmentSubmissionRepo.findAssignmentSubmissionByStudentUserID(studentId);
        return mapToDTO(submissions);
    }

    private List<AssignmentSubmissionDTO> mapToDTO(List<AssignmentSubmission> submissions) {
        return submissions.stream()
                .map(submission -> modelMapper.map(submission, AssignmentSubmissionDTO.class))
                .collect(Collectors.toList());
    }
}