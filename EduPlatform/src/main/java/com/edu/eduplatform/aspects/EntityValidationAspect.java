package com.edu.eduplatform.aspects;


import com.edu.eduplatform.services.CourseService;
import com.edu.eduplatform.services.InstructorService;
import com.edu.eduplatform.services.StudentService;
import jakarta.persistence.EntityNotFoundException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EntityValidationAspect {

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private InstructorService instructorService;

    @Before("@annotation(com.edu.eduplatform.annotations.ValidateCourse) && args(courseId,..)")
    public void validateCourse(Long courseId) {
        try {
            courseService.isCourseExists(courseId); // This will throw an exception if the course is not found
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundException("Course not found with id: " + courseId);
        }
    }

    @Before("@annotation(com.edu.eduplatform.annotations.ValidateStudent) && args(studentId,..)")
    public void validateStudent(Long studentId) {
        try{
            studentService.isStudentExists(studentId); // This will throw an exception if the student is not found
        } catch (EntityNotFoundException ex) {
            throw new EntityNotFoundException("Student not found with id: " + studentId);
        }
    }

    @Before("@annotation(com.edu.eduplatform.annotations.ValidateInstructor) && args(instructorId,..)")
    public void validateInstructor(Long instructorId) {
        instructorService.getInstructorById(instructorId);
    }
}