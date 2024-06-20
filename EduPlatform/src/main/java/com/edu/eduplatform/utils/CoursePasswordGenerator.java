package com.edu.eduplatform.utils;

import com.edu.eduplatform.utils.IUtils.ICoursePasswordGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CoursePasswordGenerator implements ICoursePasswordGenerator{

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}