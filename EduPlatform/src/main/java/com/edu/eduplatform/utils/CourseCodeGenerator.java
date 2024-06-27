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
    public String generateCode(String courseTitle) {
        String courseCode;
        do {
            courseCode = generateRandomCode(courseTitle);
        } while (courseRepository.existsByCourseCode(courseCode)); // Ensure uniqueness
        return courseCode;
    }

    private String generateRandomCode(String courseTitle) {
        StringBuilder code = new StringBuilder();
        // Append first few characters of course title (uppercase and without spaces)
        String prefix = courseTitle.toUpperCase().replaceAll("\\s+", "").substring(0, Math.min(courseTitle.length(), 5));
        code.append(prefix);
        // Append random characters to complete the code
        for (int i = prefix.length(); i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}
