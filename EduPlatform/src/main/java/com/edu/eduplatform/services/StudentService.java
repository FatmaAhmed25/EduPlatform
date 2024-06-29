package com.edu.eduplatform.services;

import com.edu.eduplatform.annotations.ValidateStudent;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.StudentRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @ValidateStudent
    public Student getStudentById(long studentId) {
        return studentRepo.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found " + studentId));
    }
  
    public boolean isStudentExists(long studentId) {
        if (!studentRepo.existsById(studentId)) {
            throw new EntityNotFoundException("Student with id " + studentId + " not found");
        }
        return true;
    }

    public Set<Course> getEnrolledCourses(long studentId){
        Student student=getStudentById(studentId);
        return student.getCourses();
    }

    public boolean isStudentEnrolledInCourse(long studentId,long courseId) {
        Student student = getStudentById(studentId);
        Set<Course> enrolledCourses = student.getCourses();

        boolean isEnrolled = enrolledCourses.stream().anyMatch(course -> course.getCourseId().equals(courseId));

        if (!isEnrolled) {
            throw new EntityNotFoundException("Student with id " + studentId + " is not enrolled in course with id " + courseId);
        }
        return true;
    }

}
