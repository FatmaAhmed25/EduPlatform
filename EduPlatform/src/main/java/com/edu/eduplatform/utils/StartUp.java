package com.edu.eduplatform.utils;

import com.edu.eduplatform.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.edu.eduplatform.services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;


@Component
public class StartUp implements ApplicationRunner {
    @Autowired
    private AuthService authService;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserDTO adminUser = new UserDTO();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@admin.com");
        adminUser.setPassword("admin");
        adminUser.setBio("I'm an admin !");
        authService.createAdmin(adminUser);
    }
}