package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.UserDTO;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.services.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @PostMapping(value = "/import/students", consumes = {"multipart/form-data"})
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) throws IOException {

        return adminService.importUsers(file, User.UserType.ROLE_STUDENT);

    }

    @PostMapping(value = "/import/instructors", consumes = {"multipart/form-data"})
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> importInstructors(@RequestParam("file") MultipartFile file) throws IOException {
           return adminService.importUsers(file, User.UserType.ROLE_INSTRUCTOR);
    }

    @PostMapping("/create/student")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> createStudent(@RequestBody UserDTO userDTO)
    {
        return adminService.createStudent(userDTO);
    }

    @PostMapping("/create/instructor")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> createInstructor(@RequestBody UserDTO userDTO)
    {
        return adminService.createInstructor(userDTO);

    }
}
