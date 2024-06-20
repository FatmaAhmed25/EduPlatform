package com.edu.eduplatform.utils;

import com.edu.eduplatform.repos.CourseRepo;
import com.edu.eduplatform.utils.IUtils.ICourseCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class CourseCodeGenerator implements ICourseCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CourseRepo courseRepository;

    @Autowired
    public CourseCodeGenerator(CourseRepo courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public String generateCode() {
        String courseCode;
        do {
            courseCode = generateRandomCode();
        } while (courseRepository.existsByCourseCode(courseCode)); // Ensure uniqueness
        return courseCode;
    }

    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
