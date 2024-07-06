package com.edu.eduplatform.services;

import com.edu.eduplatform.annotations.ValidateCourse;
import com.edu.eduplatform.dtos.CourseDTO;
import com.edu.eduplatform.dtos.CourseDetailsDTO;
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

        // Generate a unique course code based on course title
        String courseCode = courseCodeGenerator.generateCode(courseDTO.getTitle());

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


    public List<CourseResponseDTO> getCoursesCreatedByInstructor(Long instructorId)
    {
        // Check if instructor exists
        List<Course> courses = courseRepository.findByCreatedBy_UserID(instructorId);
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseResponseDTO.class))
                .collect(Collectors.toList());
    }

    // @Transactional
    public ResponseEntity<Object> enrollStudentInCourse(long courseId, long studentId, String coursePassword) {
        Course course = getCourseById(courseId);
        Student student = studentService.getStudentById(studentId);

        // Debug logging
        System.out.println("Fetched Course: " + course);
        System.out.println("Fetched Student: " + student);
        // Check if the student is already enrolled in the course
        if (course.getStudents().contains(student)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Only status code
        }

        // Check if the course password is correct
        if (!course.getPassword().equals(coursePassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Only status code
        }

        student.getCourses().add(course);
        course.getStudents().add(student);

        studentRepo.save(student);
        courseRepository.save(course);

        return ResponseEntity.ok().build();
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

    public List<Course> searchCoursesInstructor(String searchTerm, Long instructorId) {
        return courseRepository.searchByCourseCodeOrTitleAndInstructor(searchTerm, instructorId);
    }

    public List<Course> findByCourseCodeInstructor(String courseCode, Long instructorId) {
        return courseRepository.findByCourseCodeContainingIgnoreCaseAndInstructor(courseCode, instructorId);
    }

    public List<Course> findByTitleInstructor(String title, Long instructorId) {
        return courseRepository.findByTitleContainingIgnoreCaseAndInstructor(title, instructorId);
    }

    public int getEnrolledStudentCount(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return course.getStudents().size();
    }

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

    public ResponseEntity<Object> enrollStudentInCourseByCode(String courseCode, Long studentId, String coursePassword) {
        Course course = courseRepository.findByCourseCode(courseCode);
        if (course == null) {
            throw new EntityNotFoundException("Course not found with code: " + courseCode);
        }

        Student student = studentService.getStudentById(studentId);

        // Check if the student is already enrolled in the course
        if (course.getStudents().contains(student)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!course.getPassword().equals(coursePassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        student.getCourses().add(course);
        course.getStudents().add(student);

        studentRepo.save(student);
        courseRepository.save(course);

        return ResponseEntity.ok().build();
    }
    public boolean isInstructorOfCourse(Long instructorId, Long courseId) {
        Course course = getCourseById(courseId);
        if(course.getCreatedBy().getUserID() == (instructorId))
            return true;
        for(Instructor i : course.getTaInstructors())
        {
            if(instructorId == i.getUserID())
            {
                return true;
            }
        }
        return false;
    }
    public Set<Instructor> getCourseTAs(Long courseId){
        Course course = getCourseById(courseId);
        return course.getTaInstructors();
    }
    public CourseDetailsDTO getCourseDetailsById(long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        CourseDetailsDTO courseDetailsDTO = new CourseDetailsDTO();
        courseDetailsDTO.setTitle(course.getTitle());
        courseDetailsDTO.setDescription(course.getDescription());
        courseDetailsDTO.setPassword(course.getPassword());
        courseDetailsDTO.setCourseCode(course.getCourseCode());

        return courseDetailsDTO;
    }
}