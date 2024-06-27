package com.edu.eduplatform.services;

import com.edu.eduplatform.controllers.CommentWebSocketController;
import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.dtos.CreateCommentDTO;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.LogManager;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class AnnouncementService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CourseContentService courseContentService;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private InstructorRepo instructorRepo;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AnnouncementRepo announcementRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public void notifyStudent(Student student, String message) {
        rabbitTemplate.convertAndSend("notificationQueue", "Notify " + student.getUsername() + ": " + message);
    }


    @Transactional
    public Announcement  uploadMaterialAndNotifyStudents(Long instructorId, Long courseId,String folderName,MultipartFile file, AnnouncementDTO announcementDto) throws  IOException {
        // Upload the file
        String fileName = courseContentService.uploadFile(courseId.toString(), folderName, file);
        announcementDto.setFileName(fileName);
        // Create the announcement
        return createAnnouncement(courseId, instructorId, announcementDto);
    }

    @Transactional
    public Announcement createAnnouncement(Long courseId, Long instructorId, AnnouncementDTO announcementDto) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

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

        Set<Student> students = course.getStudents();
        for (Student student : students) {
            notifyStudent(student, "New announcement: " + announcementDto.getTitle() + " ->> " + announcementDto.getContent());
        }

        return announcementRepo.save(announcement);
    }


    public List<Announcement> getAnnouncementsForStudent(Long studentId) {
        Set<Course> enrolledCourses = studentService.getStudentById(studentId).getCourses();

        List<Announcement> announcements = new ArrayList<>();

        for (Course course : enrolledCourses) {
            List<Announcement> courseAnnouncements = announcementRepo.findByCourseOrderByCreatedAtDesc(course);
            announcements.addAll(courseAnnouncements);
        }

        // Sort all announcements by createdAt descending
        announcements.sort(Comparator.comparing(Announcement::getCreatedAt).reversed());

        return announcements;
    }

    public Comment addComment(CreateCommentDTO createCommentDTO) {
        // Find the Announcement by announcementId
        Announcement announcement = announcementRepo.findById(createCommentDTO.getAnnouncementId())
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        // Find the User by userId
        User user = userRepo.findById(createCommentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Map CreateCommentDTO to Comment using ModelMapper
        Comment comment = new Comment();
        comment.setContent(createCommentDTO.getCommentContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAnnouncement(announcement);
        comment.setUser(user);

        // Save the comment and update the announcement
        comment = commentRepo.save(comment);

        // Add the comment to the announcement's list of comments
        announcement.getComments().add(comment);
        announcementRepo.save(announcement);

//        messagingTemplate.convertAndSend("/topic/announcement/" + announcement.getId() + "/comments", comment);

        return comment;
    }

    @Transactional
    public List<Comment> getCommentsForAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        return commentRepo.findByAnnouncementOrderByCreatedAtAsc(announcement);
    }

}