package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.AnnouncementDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.Instructor;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.AnnouncementRepo;
import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.repos.InstructorRepo;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    CourseContentService courseContentService;

    @Autowired
    CourseRepo courseRepo;
    @Autowired
    InstructorRepo instructorRepo;

    @Autowired
    StudentService studentService;
    @Autowired
    AnnouncementRepo announcementRepo;
    @Autowired
    ModelMapper modelMapper;



    public void notifyStudent(Student student, String message) {
        rabbitTemplate.convertAndSend("notificationQueue", "Notify " + student.getUsername() + ": " + message);
    }




    @Transactional
    public void uploadMaterialAndNotifyStudents(Long instructorId, Long courseId,String folderName,MultipartFile file, AnnouncementDTO announcementDto) throws  IOException {
        // Upload the file
        String fileName =courseContentService.uploadFile(courseId.toString(), folderName, file);
        announcementDto.setFileName(fileName);
        // Create the announcement
       createAnnouncement(courseId, instructorId, announcementDto);

    }

    @Transactional
    public Announcement createAnnouncement(Long courseId, Long instructorId, AnnouncementDTO announcementDto ) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        if (!course.getCreatedBy().equals(instructor)) {
            throw new RuntimeException("Instructor not authorized to create announcement for this course");
        }

        Announcement announcement = modelMapper.map(announcementDto, Announcement.class);
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setCourse(course);
        announcement.setInstructor(instructor);

        Set<Student> students = course.getStudents();
        for (Student student : students) {
            notifyStudent(student, "New announcement : "+announcementDto.getTitle()+"->>"+announcementDto.getContent());
        }


        return announcementRepo.save(announcement);
    }


    public List<Announcement> getAnnouncementsForStudent(Long studentId) {
        // Step 1: Retrieve courses enrolled by the student
        Set<Course> enrolledCourses = studentService.getStudentById(studentId).getCourses();

        List<Announcement> announcements = new ArrayList<>();

        // Step 2: Retrieve announcements for each enrolled course and sort by createdAt descending
        for (Course course : enrolledCourses) {
            List<Announcement> courseAnnouncements = announcementRepo.findByCourseOrderByCreatedAtDesc(course);
            announcements.addAll(courseAnnouncements);
        }

        // Sort all announcements by createdAt descending
        announcements.sort(Comparator.comparing(Announcement::getCreatedAt).reversed());

        return announcements;
    }


}
