//package com.edu.eduplatform.services;
//
//import com.edu.eduplatform.dtos.AnnouncementDTO;
//import com.edu.eduplatform.dtos.AssignmentResponseDTO;
//import com.edu.eduplatform.dtos.CreateCommentDTO;
//import com.edu.eduplatform.dtos.getAnnouncementDTO;
//import com.edu.eduplatform.models.*;
//import com.edu.eduplatform.repos.*;
//import jakarta.transaction.Transactional;
//import org.modelmapper.ModelMapper;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class AnnouncementService {
//
//
//    @Autowired
//    private CourseContentService courseContentService;
//    @Autowired
//    private CourseRepo courseRepo;
//    @Autowired
//    private InstructorRepo instructorRepo;
//    @Autowired
//    private StudentService studentService;
//    @Autowired
//    private AnnouncementRepo announcementRepo;
//    @Autowired
//    private ModelMapper modelMapper;
//    @Autowired
//    private UserRepo userRepo;
//    @Autowired
//    private CommentRepo commentRepo;
//
//    @Autowired
//    private NotificationService notificationService;
//
////    @Autowired
////    private SimpMessagingTemplate messagingTemplate;
//
//
//    public Object getAnnouncementById(Long announcementId) {
//        Announcement announcement = announcementRepo.findById(announcementId)
//                .orElseThrow(() -> new RuntimeException("Announcement not found"));
//
//        // Check if the announcement is an instance of Assignment
//        if (announcement instanceof Assignment) {
//            return mapToAssignmentDTO((Assignment) announcement);
//        } else {
//            return mapToAnnouncementDTO(announcement);
//        }
//    }
//
//    private getAnnouncementDTO mapToAnnouncementDTO(Announcement announcement) {
//        return modelMapper.map(announcement, getAnnouncementDTO.class);
//    }
//
//    private AssignmentResponseDTO mapToAssignmentDTO(Assignment assignment) {
//        return modelMapper.map(assignment, AssignmentResponseDTO.class);
//    }
//
//
//
//
//    public List<Announcement> getLectureAnnouncements(Long courseId) {
//        return getAnnouncementsByFileNamePattern(courseId, "lectures/");
//    }
//
//
//    public List<Announcement> getLabAnnouncements(Long courseId) {
//        return getAnnouncementsByFileNamePattern(courseId, "labs/");
//    }
//    public List<AssignmentResponseDTO> getAssignmentAnnouncements(Long courseId) {
//        List<Announcement> announcements = announcementRepo.findAnnouncementsByFileNameStartingWithAndCourse_CourseId("assignments/", courseId);
//        return announcements.stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//    private AssignmentResponseDTO convertToDto(Announcement announcement) {
//        return modelMapper.map(announcement, AssignmentResponseDTO.class);
//    }
//
//    private List<Announcement> getAnnouncementsByFileNamePattern(Long courseId, String fileNamePattern) {
//        Course course = courseRepo.findById(courseId)
//                .orElseThrow(() -> new RuntimeException("Course not found"));
//
//        return announcementRepo.findByCourse(course).stream()
//                .filter(announcement -> announcement.getFileName() != null && announcement.getFileName().startsWith(fileNamePattern))
//                .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
//                .collect(Collectors.toList());
//    }
//
//
//
//
//    @Transactional
//    public Announcement uploadMaterialAndNotifyStudents(Long instructorId, Long courseId, MaterialType folderName, MultipartFile file, AnnouncementDTO announcementDto) throws IOException {
//        // Upload the file using the folder name from the enum
//        String fileName = courseContentService.uploadFile(courseId.toString(), folderName.getFolder(), file);
//        announcementDto.setFileName(fileName);
//
//        // Create the announcement
//        return createAnnouncement(courseId, instructorId, announcementDto);
//    }
//
//
//    @Transactional
//    public Announcement createAnnouncement(Long courseId, Long instructorId, AnnouncementDTO announcementDto) {
//        Course course = courseRepo.findById(courseId)
//                .orElseThrow(() -> new RuntimeException("Course not found"));
//
//        Instructor instructor = instructorRepo.findById(instructorId)
//                .orElseThrow(() -> new RuntimeException("Instructor not found"));
//
//        // Check if the instructorId is the course creator or a TA
//        boolean isCourseCreatorOrTA = course.getCreatedBy().getUserID() == instructorId ||
//                course.getTaInstructors().stream().anyMatch(ta -> ta.getUserID() == instructorId);
//
//        if (!isCourseCreatorOrTA) {
//            throw new RuntimeException("Instructor not authorized to create announcement for this course");
//        }
//
//        Announcement announcement = modelMapper.map(announcementDto, Announcement.class);
//        announcement.setCreatedAt(LocalDateTime.now());
//        announcement.setCourse(course);
//        announcement.setInstructor(instructor);
//
//        String notificationMessage = "New announcement: " + announcementDto.getTitle() + " ->> " + announcementDto.getContent();
//        announcement.setNotificationMessage(notificationMessage);
//
//        Set<Student> students = course.getStudents();
////        for (Student student : students) {
////            notificationService.notifyStudent(student, "New announcement: " + announcementDto.getTitle() + " ->> " + announcementDto.getContent());
////        }
//
//        return announcementRepo.save(announcement);
//    }
//
//
//
//    public List<Announcement> getNotiicationsForStudent(Long studentId) {
//        Set<Course> enrolledCourses = studentService.getStudentById(studentId).getCourses();
//
//        List<Announcement> announcements = new ArrayList<>();
//
//        for (Course course : enrolledCourses) {
//            List<Announcement> courseAnnouncements = announcementRepo.findByCourseOrderByCreatedAtDesc(course);
//            announcements.addAll(courseAnnouncements);
//        }
//
//        // Sort all announcements by createdAt descending
//        announcements.sort(Comparator.comparing(Announcement::getCreatedAt).reversed());
//
//        return announcements;
//    }
//
//
//
//    public Comment addComment(CreateCommentDTO createCommentDTO) {
//        // Find the Announcement by announcementId
//        Announcement announcement = announcementRepo.findById(createCommentDTO.getAnnouncementId())
//                .orElseThrow(() -> new RuntimeException("Announcement not found"));
//
//        // Find the User by userId
//        User user = userRepo.findById(createCommentDTO.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Map CreateCommentDTO to Comment using ModelMapper
//        Comment comment = new Comment();
//        comment.setContent(createCommentDTO.getCommentContent());
//        comment.setCreatedAt(LocalDateTime.now());
//        comment.setAnnouncement(announcement);
//        comment.setUser(user);
//
//        // Save the comment and update the announcement
//        comment = commentRepo.save(comment);
//
//        // Add the comment to the announcement's list of comments
//        announcement.getComments().add(comment);
//        announcementRepo.save(announcement);
//
////        messagingTemplate.convertAndSend("/topic/announcement/" + announcement.getId() + "/comments", comment);
//
//        return comment;
//    }
//
//    @Transactional
//    public List<Comment> getCommentsForAnnouncement(Long announcementId) {
//        Announcement announcement = announcementRepo.findById(announcementId)
//                .orElseThrow(() -> new RuntimeException("Announcement not found"));
//
//        return commentRepo.findByAnnouncementOrderByCreatedAtAsc(announcement);
//    }
//
//}