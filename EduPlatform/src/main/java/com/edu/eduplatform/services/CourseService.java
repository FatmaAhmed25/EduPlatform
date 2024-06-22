package com.edu.eduplatform.services;

import com.edu.eduplatform.annotations.ValidateCourse;
import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.dtos.CourseResponseDTO;
import com.edu.eduplatform.dtos.UpdateCourseDTO;

import com.edu.eduplatform.models.Instructor;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.repos.StudentRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.edu.eduplatform.models.Course;
import com.edu.eduplatform.utils.IUtils.ICourseCodeGenerator;
import com.edu.eduplatform.utils.IUtils.ICoursePasswordGenerator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private ICourseCodeGenerator courseCodeGenerator;

    @Autowired
    private ICoursePasswordGenerator passwordGenerator;

    @Autowired
    private InstructorService instructorService;

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    StudentService studentService;

    public boolean isCourseExists(long courseId) {
        if (!courseRepository.existsByCourseId(courseId)) {
            throw new EntityNotFoundException("Course with id " + courseId + " not found");
        }
        return true;
    }

    @ValidateCourse
    public Course getCourseById(long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
    }

    @Transactional
    public void generateCourse(Long instructorId, CourseDTO courseDTO) {

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
        Instructor instructor = instructorService.getInstructorById(instructorId);
        course.setInstructor(instructor);
        instructor.getCourses().add(course);

        // Save the course to the database
        courseRepository.save(course);


    }

    @Transactional
    public void assignTAToCourse(Long instructorId, Long courseId, Long taId) throws Exception {

        // Check if course exists
        Course course = getCourseById(courseId);

        // Validate if the instructor is the course creator
        if (course.getCreatedBy().getUserID() != instructorId) {
            throw new Exception("You are not authorized to assign TAs to this course");
        }

        // Check if instructor (TA) exists

        Instructor ta = instructorService.getInstructorById(taId);

        // Add the TA to the course
        course.getTaInstructors().add(ta);

        // Save the course with updated TA list
        courseRepository.save(course);
    }


    public List<CourseResponseDTO> getCoursesCreatedByInstructor(Long instructorId) {
        // Check if instructor exists
        List<Course> courses = courseRepository.findByCreatedBy_UserID(instructorId);
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseResponseDTO.class))
                .collect(Collectors.toList());
    }

    // @Transactional
    public ResponseEntity<?> enrollStudentInCourse(long courseId, long studentId, String coursePassword) {
        Course course = getCourseById(courseId);
        Student student = studentService.getStudentById(studentId);

        // Debug logging
        System.out.println("Fetched Course: " + course);
        System.out.println("Fetched Student: " + student);
        // Check if the student is already enrolled in the course
        if (course.getStudents().contains(student)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student is already enrolled in the course.");
        }


        if (!course.getPassword().equals(coursePassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid course password.");
        }

        student.getCourses().add(course);
        course.getStudents().add(student);

        studentRepo.save(student);
        courseRepository.save(course);

        return ResponseEntity.ok("Student enrolled successfully.");
    }

    public List<Course> searchCourses(String searchTerm) {
        return courseRepository.searchByCourseCodeOrTitle(searchTerm);
    }

    public List<Course> findByCourseCode(String courseCode) {
        return courseRepository.findByCourseCodeContainingIgnoreCase(courseCode);
    }

    public List<Course> findByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

    //    public Course updateCourse(Long courseId, UpdateCourseDTO updateCourseDTO) {
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
//        course.setTitle(updateCourseDTO.getTitle());
//        course.setDescription(updateCourseDTO.getDescription());
//        course.setPassword(updateCourseDTO.getPassword());
//        // Update other fields as necessary
//
//        return courseRepository.save(course);
//    }
    public Course updateCourse(Long courseId, UpdateCourseDTO updatedCourse) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        if (updatedCourse.getTitle() != null && !updatedCourse.getTitle().isEmpty()) {
            course.setTitle(updatedCourse.getTitle());
        }
        if (updatedCourse.getDescription() != null && !updatedCourse.getDescription().isEmpty()) {
            course.setDescription(updatedCourse.getDescription());
        }
        if (updatedCourse.getPassword() != null && !updatedCourse.getPassword().isEmpty()) {
            course.setPassword(updatedCourse.getPassword());
        }

        return courseRepository.save(course);
    }
}