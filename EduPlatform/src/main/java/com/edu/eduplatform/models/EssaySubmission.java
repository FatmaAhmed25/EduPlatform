package com.edu.eduplatform.models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name="EssaySubmissions")
public class EssaySubmission extends QuizSubmission
{

    @OneToMany(mappedBy = "essaySubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentEssayAnswer> answers;

}
