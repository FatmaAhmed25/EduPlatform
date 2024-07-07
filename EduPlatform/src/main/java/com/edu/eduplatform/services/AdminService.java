package com.edu.eduplatform.services;


import com.edu.eduplatform.dtos.UpdateUserDTO;
import com.edu.eduplatform.dtos.UserDTO;
import com.edu.eduplatform.dtos.GetCoursesDTO;
import com.edu.eduplatform.models.Instructor;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.models.UserSpecification;
import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.repos.InstructorRepo;
import com.edu.eduplatform.repos.StudentRepo;
import com.edu.eduplatform.repos.UserRepo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepo userRepository;
    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;


    @Autowired
    private StudentRepo studentRepository;

    @Autowired
    private InstructorRepo instructorRepository;


    public ResponseEntity<?> importUsers(MultipartFile file, User.UserType userType) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        StringBuilder responseMessage = new StringBuilder();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // skip header row

            String email = row.getCell(1).getStringCellValue();
            if (userRepository.existsByEmail(email)) {
                responseMessage.append("Email already exists: ").append(email).append("\n");
                continue; // skip this user and continue with the next one
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(row.getCell(0).getStringCellValue());
            userDTO.setEmail(email);
            userDTO.setPassword(encoder.encode(convertNumericPasswordToString(row.getCell(2).getNumericCellValue())));

            if (userType == User.UserType.ROLE_STUDENT) {
                Student student = modelMapper.map(userDTO, Student.class);
                student.setUserType(User.UserType.ROLE_STUDENT);
                userRepository.save(student);
                responseMessage.append("Student created: ").append(email).append("\n");
            } else if (userType == User.UserType.ROLE_INSTRUCTOR) {
                Instructor instructor = modelMapper.map(userDTO, Instructor.class);
                instructor.setUserType(User.UserType.ROLE_INSTRUCTOR);
                userRepository.save(instructor);
                responseMessage.append("Instructor created: ").append(email).append("\n");
            }
        }

        workbook.close();

        if (responseMessage.length() > 0) {
            return ResponseEntity.ok(responseMessage.toString());
        } else {
            return ResponseEntity.internalServerError().body("Request failed!");
        }
    }

    private String convertNumericPasswordToString(double numericPassword) {
        DecimalFormat df = new DecimalFormat("#");
        return df.format(numericPassword);
    }

    public ResponseEntity<?>  createStudent(UserDTO userDTO) {
        try {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                //email already exists
              return ResponseEntity.badRequest().build();
            }

            Student student = modelMapper.map(userDTO, Student.class);
            student.setUserType(User.UserType.ROLE_STUDENT);
            student.setPassword(encoder.encode(userDTO.getPassword()));
            userRepository.save(student);

            return ResponseEntity.ok().build();
        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }

    public  ResponseEntity<?>  createInstructor(UserDTO userDTO) {
        try {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                return ResponseEntity.badRequest().body("Email already exists!");
            }

            Instructor instructor = modelMapper.map(userDTO, Instructor.class);
            instructor.setUserType(User.UserType.ROLE_INSTRUCTOR);
            instructor.setPassword(encoder.encode(userDTO.getPassword()));
            userRepository.save(instructor);

            return ResponseEntity.ok().body("Instructor created successfully");
        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().body("Request failed!");
        }
    }

    public ResponseEntity<?> resetPasswordForUser(long userId, String newPassword) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found!");
            }
            user.setPassword(encoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok().body("Password reset successfully for user ID: " + userId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Request failed!");
        }
    }
    public ResponseEntity<?> updateAdminProfile(UpdateUserDTO userDTO) {
        try {
            User user = userRepository.findById(userDTO.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found!");
            }

            if(user.getUserType()== User.UserType.ROLE_ADMIN)
            {


                if (userDTO.getUsername() != null) {
                    user.setUsername(userDTO.getUsername());
                }
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail());
                }
                if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                    user.setPassword(encoder.encode(userDTO.getPassword()));
                }
                if (userDTO.getBio() != null) {
                    user.setBio(userDTO.getBio());
                }
            }
            else
            {

                return ResponseEntity.badRequest().body("this is not an admin");

            }

            userRepository.save(user);
            return ResponseEntity.ok().body("User profile updated successfully by admin");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Request failed!");
        }
    }

    public ResponseEntity<?> adminUpdateUserProfile(UpdateUserDTO userDTO) {
        try {
            User user = userRepository.findById(userDTO.getUserId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found!");
            }

            if(user.getUserType()== User.UserType.ROLE_ADMIN)
            {
                return ResponseEntity.badRequest().body("Admin cannot change admin profile");
            }
            if (userDTO.getUsername() != null) {
                user.setUsername(userDTO.getUsername());
            }
            if (userDTO.getEmail() != null) {
                user.setEmail(userDTO.getEmail());
            }
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(encoder.encode(userDTO.getPassword()));
            }
            if (userDTO.getBio() != null) {
                user.setBio(userDTO.getBio());
            }

            userRepository.save(user);
            return ResponseEntity.ok().body("User profile updated successfully by admin");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Request failed!");
        }
    }

    public long getNumberOfUsers() {
        return userRepository.countAllUsers();
    }

    public long getNumberOfStudents() {
        return userRepository.countAllStudents();
    }

    public long getNumberOfInstructors() {
        return userRepository.countAllInstructors();
    }

    public long getNumberOfCourses() {
        return courseRepo.countAllCourses();
    }
    public List<UserDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream().map(student -> modelMapper.map(student, UserDTO.class)).collect(Collectors.toList());
    }

    public List<UserDTO> getAllInstructors() {
        List<Instructor> instructors = instructorRepository.findAll();
        return instructors.stream().map(instructor -> modelMapper.map(instructor, UserDTO.class)).collect(Collectors.toList());
    }

    public UserDTO getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserDTO.class);
    }
    public List<GetCoursesDTO> getAllCoursesWithDetails() {
        return courseRepo.findAll().stream().map(course -> {
            GetCoursesDTO courseDTO = modelMapper.map(course, GetCoursesDTO.class);
            courseDTO.setNumberOfEnrolledStudents(course.getStudents().size());
            courseDTO.setCreatedByInstructorName(course.getCreatedBy().getUsername());
            courseDTO.setTasInstructorNames(course.getTaInstructors().stream()
                    .map(Instructor::getUsername)
                    .collect(Collectors.toList()));
            return courseDTO;
        }).collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(String searchTerm) {
        List<User> users;
        try {
            long userId = Long.parseLong(searchTerm);
            users = userRepository.findByUserID(userId);
        } catch (NumberFormatException e) {
            // If searchTerm is not a number, it could be userType, username, or email
            try {
                User.UserType userType = User.UserType.valueOf(searchTerm.toUpperCase());
                users = userRepository.findByUserType(userType);
            } catch (IllegalArgumentException ex) {
                // If searchTerm is not a valid userType, search by username or email
                users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm);
            }
        }
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserID());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setBio(user.getBio());
        userDTO.setUserType(user.getUserType().name());
        return userDTO;
    }
}



