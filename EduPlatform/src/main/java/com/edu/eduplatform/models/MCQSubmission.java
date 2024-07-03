package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="MCQSubmissions")
public class MCQSubmission extends QuizSubmission
{

    @OneToMany(mappedBy = "mcqSubmission", cascade = CascadeType.ALL)
    private List<StudentMCQAnswer> answers = new ArrayList<>();
}
