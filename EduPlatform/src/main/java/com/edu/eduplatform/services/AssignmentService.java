package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.dtos.AssignmentDTO;
import com.edu.eduplatform.dtos.AssignmentSubmissionDTO;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.AssignmentRepo;
import com.edu.eduplatform.repos.AssignmentSubmissionRepo;

import com.edu.eduplatform.repos.StudentRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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

    @Autowired
    private StudentService studentService;


    @Transactional
    public Assignment createAssignment(Long instructorId, Long courseId, MultipartFile file, AnnouncementDTO announcementDto, LocalDateTime dueDate, boolean allowLateSubmissions) throws IOException {
        String folderName = "assignments/instructor/" + instructorId;
        Announcement announcement = announcementService.uploadMaterialAndNotifyStudents(instructorId, courseId, folderName, file, announcementDto);

        // Use ModelMapper to map Announcement to Assignment
        Assignment assignment = modelMapper.map(announcement, Assignment.class);
        assignment.setDueDate(dueDate);
        assignment.setAllowLateSubmissions(allowLateSubmissions);

        return assignmentRepo.save(assignment);
    }


    @Transactional
    public void submitAssignment(Long studentId, Long assignmentId, MultipartFile file) throws IOException {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));


        String folderName = "assignments/" + "submissions/student" + studentId;

        String fileName = courseContentService.uploadFile(assignment.getCourse().getCourseId().toString(), folderName, file);

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setFileName(fileName);

        boolean isLate = submission.getSubmissionDate().isAfter(assignment.getDueDate());
        if (isLate && !assignment.isAllowLateSubmissions()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Late submissions are not allowed for this assignment");
        }

        submission.setLate(isLate);

        assignmentSubmissionRepo.save(submission);
    }

    public List<AssignmentDTO> getAssignmentsForStudent(Long studentId) {
        Set<Course> enrolledCourses = studentService.getStudentById(studentId).getCourses();


        List<AssignmentDTO> assignments = new ArrayList<>();

        for (Course course : enrolledCourses) {
            List<Assignment> courseAssignments = course.getAnnouncements().stream()
                    .filter(announcement -> announcement instanceof Assignment)
                    .map(announcement -> (Assignment) announcement)
                    .collect(Collectors.toList());

            // Map each Assignment to AssignmentDTO
            List<AssignmentDTO> courseAssignmentDTOs = courseAssignments.stream()
                    .map(assignment -> new AssignmentDTO(
                            assignment.getTitle(),
                            assignment.getContent(),
                            assignment.getDueDate(),
                            assignment.getFileName()))
                    .sorted(Comparator.comparing(AssignmentDTO::getDueDate).reversed()) // Sort by dueDate descending
                    .collect(Collectors.toList());

            assignments.addAll(courseAssignmentDTOs);
        }

        return assignments;
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