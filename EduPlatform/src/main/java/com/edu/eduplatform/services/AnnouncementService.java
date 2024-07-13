package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.*;
import com.edu.eduplatform.models.*;
import com.edu.eduplatform.repos.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {


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


    public Object getAnnouncementById(Long announcementId) {
        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        // Check if the announcement is an instance of Assignment
        if (announcement instanceof Assignment) {
            return mapToAssignmentDTO((Assignment) announcement);
        } else {
            return mapToAnnouncementDTO(announcement);
        }
    }

    private getAnnouncementDTO mapToAnnouncementDTO(Announcement announcement) {
        return modelMapper.map(announcement, getAnnouncementDTO.class);
    }

    private AssignmentResponseDTO mapToAssignmentDTO(Assignment assignment) {
        return modelMapper.map(assignment, AssignmentResponseDTO.class);
    }

    public List<Announcement> getLectureAnnouncements(Long courseId) {
        return getAnnouncementsByFileNamePattern(courseId, "lectures/");
    }

    public boolean isAssignment(long id) {
        Optional<Announcement> announcementOptional = announcementRepo.findById(id);

        if (announcementOptional.isPresent()) {
            Announcement announcement = announcementOptional.get();
            return announcement instanceof Assignment;
        }

        return false; // Handle case where announcement with given id doesn't exist
    }


    public List<Announcement> getLabAnnouncements(Long courseId) {
        return getAnnouncementsByFileNamePattern(courseId, "labs/");
    }
    public List<AssignmentResponseDTO> getAssignmentAnnouncements(Long courseId) {
        List<Announcement> announcements = announcementRepo.findAnnouncementsByFileNameStartingWithAndCourse_CourseId("assignments/", courseId);
        return announcements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AssignmentResponseDTO convertToDto(Announcement announcement) {
        return modelMapper.map(announcement, AssignmentResponseDTO.class);
    }

    private List<Announcement> getAnnouncementsByFileNamePattern(Long courseId, String fileNamePattern) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return announcementRepo.findByCourse(course).stream()
                .filter(announcement -> announcement.getFileName() != null && announcement.getFileName().startsWith(fileNamePattern))
                .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }




    @Transactional
    public Announcement uploadMaterialAndNotifyStudents(Long instructorId, Long courseId, MaterialType folderName, MultipartFile file, AnnouncementDTO announcementDto) throws IOException {
        // Upload the file using the folder name from the enum
        String fileName = courseContentService.uploadFile(courseId.toString(), folderName.getFolder(), file);
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

        String notificationMessage = announcement.getInstructor().getUsername()+" "+announcement.getCourse().getTitle()+" "+announcementDto.getTitle() + " " + announcementDto.getContent();
        announcement.setNotificationMessage(notificationMessage);

        Announcement saveAnnouncement = announcementRepo.save(announcement);

        NotificationDTO notificationDTO = new NotificationDTO(saveAnnouncement.getId(),notificationMessage);
        messagingTemplate.convertAndSend("/topic/course/" + courseId, notificationDTO);

        return saveAnnouncement;
    }

    @Transactional
    public Announcement updateAnnouncement(Long courseId, Long instructorId, Long announcementId,
                                           String title, String content, MaterialType materialType,
                                           MultipartFile file) throws IOException {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        boolean isCourseCreatorOrTA = course.getCreatedBy().getUserID() == instructorId ||
                course.getTaInstructors().stream().anyMatch(ta -> ta.getUserID() == instructorId);

        if (!isCourseCreatorOrTA) {
            throw new RuntimeException("Instructor not authorized to update announcement for this course");
        }

        if (title != null) {
            announcement.setTitle(title);
        }
        if (content != null) {
            announcement.setContent(content);
        }

        if (file != null && materialType != null) {
            // Delete the old file if it exists
            if (announcement.getFileName() != null) {
                courseContentService.deleteFile(courseId.toString(), announcement.getFileName());
            }
            // Upload the new file
            String newFileName = courseContentService.uploadFile(courseId.toString(), materialType.getFolder(), file);
            announcement.setFileName(newFileName);
        }

        announcement.setCreatedAt(LocalDateTime.now());

        String notificationMessage = announcement.getInstructor().getUsername() + " " + announcement.getCourse().getTitle() + " " + announcement.getTitle() + " " + announcement.getContent();
        announcement.setNotificationMessage(notificationMessage);

        Announcement savedAnnouncement = announcementRepo.save(announcement);

        NotificationDTO notificationDTO = new NotificationDTO(savedAnnouncement.getId(), notificationMessage);
        messagingTemplate.convertAndSend("/topic/course/" + courseId, notificationDTO);

        return savedAnnouncement;
    }


    @Transactional
    public void deleteAnnouncement(Long courseId, Long instructorId, Long announcementId) throws IOException {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        boolean isCourseCreatorOrTA = course.getCreatedBy().getUserID() == instructorId ||
                course.getTaInstructors().stream().anyMatch(ta -> ta.getUserID() == instructorId);

        if (!isCourseCreatorOrTA) {
            throw new RuntimeException("Instructor not authorized to delete announcement for this course");
        }

        // Delete the file if it exists
        if (announcement.getFileName() != null) {
            courseContentService.deleteFile(courseId.toString(), announcement.getFileName());
        }

        announcementRepo.delete(announcement);
    }


    public List<Announcement> getNotiicationsForStudent(Long studentId) {
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

    public List<getCommentDTO> getCommentsForAnnouncement(Long announcementId) {
        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        List<Comment> comments = commentRepo.findByAnnouncementOrderByCreatedAtAsc(announcement);

        return comments.stream()
                .map(comment -> {
                    getCommentDTO dto = modelMapper.map(comment, getCommentDTO.class);
                    UserCommentDTO userDto = new UserCommentDTO();
                    userDto.setUserId(comment.getUser().getUserID());
                    userDto.setUserName(comment.getUser().getUsername());
                    dto.setUserCommentDTO(userDto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<Announcement> getVideoAnnouncements(Long courseId) {
        return getAnnouncementsByFileNamePattern(courseId, "videos/");
    }
    public Object getAnnouncementDetailsById(Long announcementId) {
        Announcement announcement = announcementRepo.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        if (announcement instanceof Assignment) {
            return modelMapper.map(announcement, AssignmentDTO.class);
        } else {
            return modelMapper.map(announcement, AnnouncementDTO.class);
        }
    }
//    public boolean isAssignment(long id) {
//        Optional<Announcement> announcementOptional = announcementRepo.findById(id);
//
//        if (announcementOptional.isPresent()) {
//            Announcement announcement = announcementOptional.get();
//            return announcement instanceof Assignment;
//        }
//
//        return false; // Handle case where announcement with given id doesn't exist
//    }
}
