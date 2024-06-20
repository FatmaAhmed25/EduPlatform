package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.models.Instructor;
import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.repos.InstructorRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.utils.IUtils.ICourseCodeGenerator;
import com.edu.eduplatform.utils.IUtils.ICoursePasswordGenerator;

import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private  CourseRepo courseRepository;

    @Autowired
    private ICourseCodeGenerator courseCodeGenerator;

    @Autowired
    private ICoursePasswordGenerator passwordGenerator;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    private  InstructorRepo instructorRepo;





    @Transactional
    public void generateCourse(Long instructorId, CourseDTO courseDTO)
    {

        // Check if instructor exists
        if (!instructorService.isInstructorExists(instructorId)) {
            throw new EntityNotFoundException("Instructor not found with ID: " + instructorId);
        }

        // Generate a unique course code
        String courseCode = courseCodeGenerator.generateCode();

        // Generate a secure password
        String password = passwordGenerator.generatePassword();

        // Create a new course
        Course course = modelMapper.map(courseDTO, Course.class);
        course.setCourseCode(courseCode);
        course.setPassword(password);
        Instructor instructor=instructorService.getInstructorById(instructorId);
        course.setInstructor(instructor);
        instructor.getCourses().add(course);

        // Save the course to the database
        courseRepository.save(course);
        instructorRepo.save(instructor);

    }
}
