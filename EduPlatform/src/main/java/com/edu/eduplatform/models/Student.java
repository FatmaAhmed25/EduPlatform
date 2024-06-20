package com.edu.eduplatform.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@DiscriminatorValue("Student")

public class Student extends User{


    @ManyToMany(mappedBy = "students" , fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();
}