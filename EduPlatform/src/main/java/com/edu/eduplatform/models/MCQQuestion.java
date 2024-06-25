package com.edu.eduplatform.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MCQ")
@Data
public class MCQQuestion extends Question {
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers= new ArrayList<>();

}
