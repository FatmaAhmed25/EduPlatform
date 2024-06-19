package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepo extends JpaRepository<Course,Long> {
    boolean existsByCourseCode(String courseCode);
}
