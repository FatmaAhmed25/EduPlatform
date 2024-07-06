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
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN c.taInstructors ta " +
            "WHERE (LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (c.createdBy.userID = :instructorId OR ta.userID = :instructorId)")
    List<Course> searchByCourseCodeOrTitleAndInstructor(@Param("searchTerm") String searchTerm, @Param("instructorId") Long instructorId);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN c.taInstructors ta " +
            "WHERE LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :courseCode, '%')) " +
            "AND (c.createdBy.userID = :instructorId OR ta.userID = :instructorId)")
    List<Course> findByCourseCodeContainingIgnoreCaseAndInstructor(@Param("courseCode") String courseCode, @Param("instructorId") Long instructorId);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN c.taInstructors ta " +
            "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "AND (c.createdBy.userID = :instructorId OR ta.userID = :instructorId)")
    List<Course> findByTitleContainingIgnoreCaseAndInstructor(@Param("title") String title, @Param("instructorId") Long instructorId);
    Course findByCourseCode(String courseCode);

    @Query("SELECT COUNT(c) FROM Course c")
    long countAllCourses();

}
