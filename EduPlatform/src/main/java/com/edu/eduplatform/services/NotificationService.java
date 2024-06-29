package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.NotificationDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.AnnouncementRepo;
import com.edu.eduplatform.repos.StudentRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {



    @Autowired
    private AnnouncementRepo announcementRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StudentService studentService;


    public List<NotificationDTO> getNotificationsForStudents(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        List<NotificationDTO> notifcationDTOs = new ArrayList<>();

        // Iterate through each course of the student
        for (Course course : student.getCourses()) {
            List<Announcement> announcements = announcementRepository.findByCourse_CourseId(course.getCourseId());

            // Mapping
            List<NotificationDTO> dtos = announcements.stream()
                    .map(announcement -> modelMapper.map(announcement, NotificationDTO.class))
                    .collect(Collectors.toList());

            notifcationDTOs.addAll(dtos);
        }

        return notifcationDTOs;
    }

}
