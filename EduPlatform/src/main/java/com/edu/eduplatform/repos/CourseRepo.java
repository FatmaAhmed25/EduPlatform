package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course,Long> {
    boolean existsByCourseCode(String courseCode);
    boolean existsByCourseId(long courseId);



    List<Course> findByCreatedBy_UserID(Long instructorId);
}
