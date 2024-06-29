package com.edu.eduplatform.models;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="StudentAnswer")
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String answer;

    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_submission_id", nullable = false)
    private EssaySubmission essaySubmission;






}
