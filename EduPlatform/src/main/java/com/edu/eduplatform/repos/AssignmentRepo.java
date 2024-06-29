package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AssignmentRepo extends JpaRepository<Assignment,Long>{

}