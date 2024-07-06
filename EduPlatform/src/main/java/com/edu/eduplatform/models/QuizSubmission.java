package com.edu.eduplatform.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "Dtype")
@Table(name="QuizSubmissions")
public class QuizSubmission
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "submissionTime", nullable = false)
    private LocalDateTime submissionTime=LocalDateTime.now();

    @Column(name = "totalGrade")
    private double totalGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private QuizSubmission.CheatingStatus cheatingStatus= QuizSubmission.CheatingStatus.PASSED;

    @OneToOne(mappedBy = "quizSubmission", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private CheatingReport cheatingReport;


    public enum CheatingStatus {
        CHEATING,
        SUSPICIOUS,
        PASSED
    }

}