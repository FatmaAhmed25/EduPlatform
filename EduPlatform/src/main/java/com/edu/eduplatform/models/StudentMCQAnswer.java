package com.edu.eduplatform.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class StudentMCQAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    private int grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private MCQSubmission mcqSubmission;

    private boolean isCorrect;
}
