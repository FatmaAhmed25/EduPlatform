package com.edu.eduplatform.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="MCQSubmissions")
public class MCQSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private LocalDateTime submissionTime;

    private double totalScore;

    @OneToMany(mappedBy = "mcqSubmission", cascade = CascadeType.ALL)
    private List<StudentMCQAnswer> answers = new ArrayList<>();
}
