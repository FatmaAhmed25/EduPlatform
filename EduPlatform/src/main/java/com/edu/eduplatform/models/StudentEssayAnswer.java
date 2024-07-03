package com.edu.eduplatform.models;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="StudentEssayAnswer")
public class StudentEssayAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;


    private String answer;


    private double grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_submission_id", nullable = false)
    private EssaySubmission essaySubmission;






}
