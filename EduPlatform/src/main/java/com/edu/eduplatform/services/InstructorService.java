package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.AnnouncementDto;
import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.dtos.UpdateInstructorDTO;
import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.Instructor;
import com.edu.eduplatform.repos.AnnouncementRepo;
import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.repos.InstructorRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepo instructorRepository;
    @Autowired
    private AnnouncementRepo announcementRepository;

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private ModelMapper modelMapper;


    public boolean isInstructorExists(Long instructorId) {
        return instructorRepository.existsById(instructorId);
    }

    public Instructor getInstructorById(Long instructorId) {
        return instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found with ID: " + instructorId));
    }


    public List<Course> getCoursesByInstructorId(Long instructorId) {
        Optional<Instructor> instructorOptional = instructorRepository.findById(instructorId);
        if (instructorOptional.isPresent()) {
            Instructor instructor = instructorOptional.get();
            return instructor.getCourses();
        }
        return null;
    }

//    @Transactional
//    public void createCourse(Long instructorId, CourseDTO courseDTO) {
//        Optional<Instructor> instructorOptional = instructorRepository.findById(instructorId);
//        if (instructorOptional.isPresent()) {
//            Instructor instructor = instructorOptional.get();
//            Course course = modelMapper.map(courseDTO, Course.class);
//            course.setInstructor(instructor);
//            instructor.getCourses().add(course);
//            instructorRepository.save(instructor);
//        }
//        else
//        {
//            throw new RuntimeException("Instructor not found.");
//        }
//    }
    public Instructor updateInstructor(Long instructorId, UpdateInstructorDTO updateInstructorDTO) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntityNotFoundException("Instructor not found"));
        if (updateInstructorDTO.getUsername() != null && !updateInstructorDTO.getUsername().isEmpty()) {
            instructor.setUsername(updateInstructorDTO.getUsername());
        }
        if (updateInstructorDTO.getEmail() != null && !updateInstructorDTO.getEmail().isEmpty()) {
            instructor.setEmail(updateInstructorDTO.getEmail());
        }
        if (updateInstructorDTO.getBio() != null && !updateInstructorDTO.getBio().isEmpty()) {
            instructor.setBio(updateInstructorDTO.getBio());
        }
        return instructorRepository.save(instructor);
    }


    public List<Announcement> getAnnouncementsByCourse(Long courseId) {
        return announcementRepository.findByCourse_CourseId(courseId);
    }
}
