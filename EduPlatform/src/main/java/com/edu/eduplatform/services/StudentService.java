package com.edu.eduplatform.services;

import com.edu.eduplatform.annotations.ValidateStudent;
import com.edu.eduplatform.models.Student;
import com.edu.eduplatform.repos.StudentRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepo studentRepo;

    @ValidateStudent
    public Student getStudentById(long studentId) {
        return studentRepo.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));
    }
    public boolean isStudentExists(long studentId) {
        if (!studentRepo.existsById(studentId)) {
            throw new EntityNotFoundException("Student with id " + studentId + " not found");
        }
        return true;
    }
}
