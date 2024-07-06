package com.edu.eduplatform.controllers;


import com.edu.eduplatform.dtos.UpdateUserDTO;
import com.edu.eduplatform.dtos.UserDTO;
import com.edu.eduplatform.dtos.GetCoursesDTO;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.services.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

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


    @PostMapping("/reset-password")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> resetPassword(@RequestParam long userId, @RequestParam String newPassword) {
        return adminService.resetPasswordForUser(userId, newPassword);
    }


    @PutMapping("/update-profile")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> adminUpdateUserProfile(@RequestBody UpdateUserDTO userDTO) {
        return adminService.adminUpdateUserProfile(userDTO);
    }

    @PutMapping("/update-my-profile")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> adminUpdateMyProfile(@RequestBody UpdateUserDTO userDTO) {
        return adminService.updateAdminProfile(userDTO);
    }
    @GetMapping("/number-of-users")
    @SecurityRequirement(name="BearerAuth")
    public long getNumberOfUsers() {
        return adminService.getNumberOfUsers();
    }

    @GetMapping("/number-of-students")
    @SecurityRequirement(name="BearerAuth")
    public long getNumberOfStudents() {
        return adminService.getNumberOfStudents();
    }

    @GetMapping("/number-of-instructors")
    @SecurityRequirement(name="BearerAuth")
    public long getNumberOfInstructors() {
        return adminService.getNumberOfInstructors();
    }

    @GetMapping("/number-of-courses")
    @SecurityRequirement(name="BearerAuth")
    public long getNumberOfCourses() {
        return adminService.getNumberOfCourses();
    }

    @GetMapping("/students")
    @SecurityRequirement(name="BearerAuth")
    public List<UserDTO> getAllStudents() {
        return adminService.getAllStudents();
    }

    @GetMapping("/instructors")
    @SecurityRequirement(name="BearerAuth")
    public List<UserDTO> getAllInstructors() {
        return adminService.getAllInstructors();
    }

    @GetMapping("/users/{id}")
    @SecurityRequirement(name="BearerAuth")
    public UserDTO getUserById(@PathVariable long id) {
        return adminService.getUserById(id);
    }
    @GetMapping("/courses/details")
    @SecurityRequirement(name="BearerAuth")
    public List<GetCoursesDTO> getAllCoursesWithDetails() {
        return adminService.getAllCoursesWithDetails();
    }

    @GetMapping("/searchUsers")
    @SecurityRequirement(name="BearerAuth")
    public List<UserDTO> searchUsers(@RequestParam String searchTerm) {
        return adminService.searchUsers(searchTerm);
    }



}
