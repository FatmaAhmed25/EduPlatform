package com.edu.eduplatform.services;


import com.edu.eduplatform.dtos.UserDTO;
import com.edu.eduplatform.models.Admin;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.repos.AdminRepo;
import com.edu.eduplatform.repos.InstructorRepo;
import com.edu.eduplatform.repos.StudentRepo;
import com.edu.eduplatform.repos.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    @Autowired
    AdminRepo adminRepo;
    @Autowired
    StudentRepo studentRepo;
    @Autowired
    InstructorRepo instructorRepo;
    @Autowired
    UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    PasswordEncoder encoder;

    public boolean createAdmin(UserDTO adminDTO) {
        if (adminRepo.existsByEmail(adminDTO.getEmail())) {
           return false;
        }
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        admin.setUserType(User.UserType.ROLE_ADMIN);
        admin.setPassword(encoder.encode(adminDTO.getPassword()));
        adminRepo.save(admin);
        return true;
    }

    public User getUserByEmail (String email)
    {
        return userRepo.findByEmail(email).get();
    }

}
