package com.edu.eduplatform.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "AssignmentSubmissions")
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FileName")
    private String fileName;

    @Column(name = "SubmissionDate", nullable = false)
    private LocalDateTime submissionDate;

    @ManyToOne
    @JoinColumn(name = "AssignmentID", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "StudentID", nullable = false)
    private Student student;


}