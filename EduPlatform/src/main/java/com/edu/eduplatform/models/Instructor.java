package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@DiscriminatorValue("Instructor")
public class Instructor extends User {

    @JsonBackReference
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;

    @JsonManagedReference
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Assignment> assignments = new HashSet<>();

    @ManyToMany(mappedBy = "taInstructors", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Course> taCourses = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Announcement> announcements = new HashSet<>();

    @Override
    public String toString() {
        return "HII";
    }
}
