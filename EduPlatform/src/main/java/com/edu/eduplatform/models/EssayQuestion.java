package com.edu.eduplatform.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("ESSAY")
@Data
public class EssayQuestion extends Question {

}
