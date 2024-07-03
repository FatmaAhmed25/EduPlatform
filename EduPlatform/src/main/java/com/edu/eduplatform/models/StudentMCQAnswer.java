package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="StudentMCQAnswer")
public class StudentMCQAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonIgnore

    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    @JsonIgnore

    private Answer answer;

    private int grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    @JsonIgnore
    private MCQSubmission mcqSubmission;

    private boolean isCorrect;
}
