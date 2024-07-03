package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CheatingReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_submission_id", nullable = true)
    @JsonIgnore

    private QuizSubmission quizSubmission;

    @Column(nullable = true)
    private String folderName;

    public void setFolderName() {
        this.folderName = "courses/courseId-" + quizSubmission.getQuiz().getCourse().getCourseId()
                +"/submission-reports"
                +"/quizId-"+quizSubmission.getQuiz().getQuizId() + "/"
                +"studentId-"+ quizSubmission.getStudent().getUserID();
    }
}