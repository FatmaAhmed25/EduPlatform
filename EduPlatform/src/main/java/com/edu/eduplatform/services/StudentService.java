package com.edu.eduplatform.services;


import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.StudentRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {


    @Autowired
    StudentRepo studentRepo;


    public Student getStudentById(long studentId)
    {
        return studentRepo.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));
    }




}
