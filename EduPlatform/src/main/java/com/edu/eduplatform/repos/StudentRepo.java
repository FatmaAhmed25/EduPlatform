package com.edu.eduplatform.repos;


import com.edu.eduplatform.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepo extends JpaRepository<Student,Long> {


    boolean existsById(Long studentId);
    List<Student> findAll();
}
