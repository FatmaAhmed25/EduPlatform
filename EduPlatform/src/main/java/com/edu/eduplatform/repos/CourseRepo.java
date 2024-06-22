package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course,Long> {
    boolean existsByCourseCode(String courseCode);
    boolean existsByCourseId(long courseId);



    List<Course> findByCreatedBy_UserID(Long instructorId);

    List<Course> findByCourseCodeContainingIgnoreCase(String courseCode);

    List<Course> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT c FROM Course c WHERE LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Course> searchByCourseCodeOrTitle(@Param("searchTerm") String searchTerm);

}
