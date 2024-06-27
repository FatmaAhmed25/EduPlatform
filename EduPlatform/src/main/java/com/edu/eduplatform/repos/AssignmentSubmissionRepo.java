package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentSubmissionRepo  extends JpaRepository<AssignmentSubmission,Long> {
    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

    List<AssignmentSubmission> findAssignmentSubmissionByStudentUserID(Long studentId);
}