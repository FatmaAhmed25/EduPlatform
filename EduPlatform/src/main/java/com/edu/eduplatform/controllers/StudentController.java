package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.StudentDTO;
import com.edu.eduplatform.dtos.UserDTO;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.StudentRepo;
import com.edu.eduplatform.repos.UserRepo;
import com.edu.eduplatform.services.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/students")
public class StudentController
{
    private StudentRepo userRepo;
    @Autowired
    StudentService studentService;
    @GetMapping("/enrolled-courses/{studentId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Set<Course> getEnrolledCourses(@PathVariable long studentId)
    {
        return studentService.getEnrolledCourses(studentId);
    }

    @GetMapping("/details/{userId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    public UserDTO getUserDetails(@PathVariable long userId) {
        return studentService.getUserDetails(userId);
    }

    @GetMapping("/by-course/{courseId}")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<StudentDTO> getStudentsByCourseId(@PathVariable long courseId) {
        return studentService.getStudentsByCourseId(courseId);
    }
}
