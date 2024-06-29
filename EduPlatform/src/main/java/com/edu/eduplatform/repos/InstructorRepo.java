package com.edu.eduplatform.repos;


import com.edu.eduplatform.models.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepo extends JpaRepository<Instructor,Long>
{

    boolean existsById(Long instructorId);



}
