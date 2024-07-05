package com.edu.eduplatform.aspects;


import com.edu.eduplatform.services.CourseService;
import com.edu.eduplatform.services.InstructorService;
import com.edu.eduplatform.services.QuizService;
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
    private QuizService quizService;

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
        try{
            instructorService.getInstructorById(instructorId);
        }
        catch (EntityNotFoundException ex) {
            throw new EntityNotFoundException("Instructor not found with id: " + instructorId);
        }
    }

    @Before("@annotation(com.edu.eduplatform.annotations.ValidateQuiz) && args(quizId,..)")
    public void validateQuiz(Long quizId) {
        try{
            quizService.isQuizExists(quizId);
        }
        catch (EntityNotFoundException ex) {
            throw new EntityNotFoundException("Quiz not found with id: " + quizId);
        }
    }

    @Before("@annotation(com.edu.eduplatform.annotations.ValidateInstructorBelongsToCourse) && args(instructorId, courseId, ..)")
    public void validateInstructorBelongsToCourse(Long instructorId, Long courseId) {
        boolean isValid = courseService.isInstructorOfCourse(instructorId, courseId);
        if (!isValid) {
            throw new EntityNotFoundException("Instructor does not have permission to modify this course.");
        }

    }
    @Before("@annotation(com.edu.eduplatform.annotations.ValidateStudentEnrollmentInCourse) && args(studentId, courseId, ..)")
    public void validateStudentBelongsToCourse(Long studentId, Long courseId) throws Exception {
        boolean isValid = studentService.isStudentEnrolledInCourse(studentId, courseId);
        if (!isValid) {
            throw new EntityNotFoundException("Student does not have permission to access this course.");
        }

    }

//    @Before("@annotation(com.edu.eduplatform.annotations.ValidateQuizBelongsToCourse) && args(courseId,quizId, ..)")
//    public void validateQuizBelongsToCourse(Long courseId, Long quizId) throws Exception {
//        boolean isValid = quizService.validateQuizBelongsToCourse(courseId,quizId);
//        if (!isValid) {
//            throw new EntityNotFoundException("Quiz does not belong to this course.");
//        }
//
//    }

}

