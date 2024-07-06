package com.edu.eduplatform.repos;


import com.edu.eduplatform.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.List;

@Repository
public interface StudentRepo extends JpaRepository<Student,Long> {


    boolean existsById(Long studentId);
    List<Student> findAll();

    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.courseId = :courseId")
    List<Student> findStudentsByCourseId(@Param("courseId") Long courseId);

}
