package com.edu.eduplatform.services;

import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.repos.CourseRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.utils.IUtils.ICourseCodeGenerator;
import com.edu.eduplatform.utils.IUtils.ICoursePasswordGenerator;

@Service
public class CourseService {
    @Autowired
    private ModelMapper modelMapper;
    private final CourseRepo courseRepository;
    private final ICourseCodeGenerator courseCodeGenerator;
    private final ICoursePasswordGenerator passwordGenerator;

    @Autowired
    public CourseService(CourseRepo courseRepository, ICourseCodeGenerator courseCodeGenerator, ICoursePasswordGenerator passwordGenerator) {
        this.courseRepository = courseRepository;
        this.courseCodeGenerator = courseCodeGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    public CourseDTO generateCourse(String title, String description) {
        // Generate a unique course code
        String courseCode = courseCodeGenerator.generateCode();

        // Generate a secure password
        String password = passwordGenerator.generatePassword();

        // Create a new course
        Course course = new Course();
        course.setCourseCode(Long.valueOf(courseCode));
        course.setTitle(title);
        course.setDescription(description);
        course.setPassword(password);

        // Save the course to the database
        Course savedCourse = courseRepository.save(course);

        // Convert to DTO
        return modelMapper.map(savedCourse, CourseDTO.class);
    }
}
