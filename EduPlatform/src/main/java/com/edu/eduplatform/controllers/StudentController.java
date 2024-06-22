package com.edu.eduplatform.controllers;


import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.services.StudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/students")
public class StudentController
{

    @Autowired
    StudentService studentService;
    @GetMapping("/enrolled-courses/{studentId}")
    @SecurityRequirement(name="BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Set<Course> getEnrolledCourses(@PathVariable long studentId)
    {
        return studentService.getEnrolledCourses(studentId);
    }
}
