package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
//@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//@ToString(onlyExplicitlyIncluded = true)
public class Quiz {
    @Id
//    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId", nullable = false)
    @JsonIgnore
    @JsonBackReference
    private Course course;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
}
