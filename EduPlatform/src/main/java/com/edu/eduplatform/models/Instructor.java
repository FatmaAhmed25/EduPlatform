package com.edu.eduplatform.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@DiscriminatorValue("Instructor")
public class Instructor extends User{

    // One-to-many relationship for courses created by this instructor
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Course> courses;

    // Many-to-many relationship for courses where this instructor is a TA
    @ManyToMany(mappedBy = "taInstructors", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Course> taCourses = new HashSet<>();

}