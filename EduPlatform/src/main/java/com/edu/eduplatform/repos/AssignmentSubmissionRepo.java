package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Assignment;
import com.edu.eduplatform.models.AssignmentSubmission;
import com.edu.eduplatform.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentSubmissionRepo  extends JpaRepository<AssignmentSubmission,Long> {
    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

    List<AssignmentSubmission> findAssignmentSubmissionByStudentUserID(Long studentId);

    AssignmentSubmission findByAssignmentAndStudent(Assignment assignment, Student student);
}