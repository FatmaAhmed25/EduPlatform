package com.edu.eduplatform.services;


import com.edu.eduplatform.dtos.UserDTO;
import com.edu.eduplatform.models.Instructor;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.repos.UserRepo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.DecimalFormat;

@Service
public class AdminService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    PasswordEncoder encoder;


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
              return ResponseEntity.badRequest().body("Email already exists!");
            }

            Student student = modelMapper.map(userDTO, Student.class);
            student.setUserType(User.UserType.ROLE_STUDENT);
            student.setPassword(encoder.encode(userDTO.getPassword()));
            userRepository.save(student);

            return ResponseEntity.ok().body("student created successfully");
        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().body("Request failed!");
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

}
