package com.edu.eduplatform.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@DiscriminatorValue("Instructor")
public class Instructor extends User{



    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Course> courses;

}