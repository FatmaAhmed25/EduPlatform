package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.Instructor;
import com.edu.eduplatform.repos.InstructorRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepo instructorRepository;

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

    @Transactional
    public void createCourse(Long instructorId, CourseDTO courseDTO) {
        Optional<Instructor> instructorOptional = instructorRepository.findById(instructorId);
        if (instructorOptional.isPresent()) {
            Instructor instructor = instructorOptional.get();
            Course course = modelMapper.map(courseDTO, Course.class);
            course.setInstructor(instructor);
            instructor.getCourses().add(course);
            instructorRepository.save(instructor);
        }
        else
        {
            throw new RuntimeException("Instructor not found.");
        }
    }


}
