package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.dtos.AssignmentDTO;
import com.edu.eduplatform.dtos.AssignmentSubmissionDTO;
import com.edu.eduplatform.dtos.NotificationDTO;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private InstructorRepo instructorRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Assignment createAssignment(Long instructorId, Long courseId, MultipartFile file, AnnouncementDTO announcementDto, LocalDateTime dueDate, boolean allowLateSubmissions) throws IOException {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        // Upload the file
        String fileName = courseContentService.uploadFile(courseId.toString(), MaterialType.ASSIGNMENTS.getFolder(), file);
        announcementDto.setFileName(fileName);

        // Check if the instructorId is the course creator or a TA
        boolean isCourseCreatorOrTA = course.getCreatedBy().getUserID() == instructorId ||
                course.getTaInstructors().stream().anyMatch(ta -> ta.getUserID() == instructorId);

        if (!isCourseCreatorOrTA) {
            throw new RuntimeException("Instructor not authorized to create announcement for this course");
        }

        Announcement announcement = modelMapper.map(announcementDto, Announcement.class);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setCourse(course);
        announcement.setInstructor(instructor);

        String notificationMessage = "New announcement: " + announcementDto.getTitle() + " ->> " + announcementDto.getContent();
        announcement.setNotificationMessage(notificationMessage);
        Assignment assignment = modelMapper.map(announcement, Assignment.class);
        assignment.setDueDate(dueDate);
        assignment.setAllowLateSubmissions(allowLateSubmissions);

        Assignment savedAssignment = assignmentRepo.save(assignment);


        NotificationDTO notificationDTO = new NotificationDTO(savedAssignment.getId(),notificationMessage);
        messagingTemplate.convertAndSend("/topic/course/" + courseId, notificationDTO);


        return savedAssignment;
    }


    @Transactional
    public void submitAssignment(Long studentId, Long assignmentId, MultipartFile file) throws IOException {
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Check for existing submission
        AssignmentSubmission existingSubmission = assignmentSubmissionRepo.findByAssignmentAndStudent(assignment, student);
        if (existingSubmission != null) {
            // Delete the old file from Firebase
            courseContentService.deleteFile(assignment.getCourse().getCourseId().toString(), existingSubmission.getFileName());

            // Delete the old submission from the repository
            assignmentSubmissionRepo.delete(existingSubmission);
        }

        // Upload new file
        String folderName = "assignments/submissions/student" + studentId;
        String fileName = courseContentService.uploadFile(assignment.getCourse().getCourseId().toString(), folderName, file);

        // Create new submission
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

        // Save new submission
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