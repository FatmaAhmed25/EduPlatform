package com.edu.eduplatform.controllers;

import com.edu.eduplatform.dtos.AdminDTO;
import com.edu.eduplatform.dtos.AuthDTO;
import com.edu.eduplatform.dtos.AuthResponseDTO;
import com.edu.eduplatform.models.User;
import com.edu.eduplatform.security.JwtService;
import com.edu.eduplatform.services.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @SecurityRequirement(name="BearerAuth")
    public ResponseEntity<?> createAdmin(@RequestBody AdminDTO adminDTO) {
        boolean isAdminCreated = authService.createAdmin(adminDTO);
        if (isAdminCreated) {
            return ResponseEntity.ok().body("Admin created successfully");
        } else {
            return ResponseEntity.badRequest().body("Request failed !");
        }
    }

    @PostMapping("/authenticate")
    public AuthResponseDTO authenticateAndGetToken(@RequestBody AuthDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            User user = authService.getUserByEmail(authRequest.getEmail());
            AuthResponseDTO responseDTO =  modelMapper.map(user,AuthResponseDTO.class);
            responseDTO.setToken(jwtService.generateToken(authRequest.getEmail()));
            return responseDTO;
        } else {
            throw new UsernameNotFoundException("Invalid user request !");
        }
    }
}
