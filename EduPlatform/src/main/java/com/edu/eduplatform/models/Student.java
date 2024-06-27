package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@DiscriminatorValue("Student")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Student extends User {

    @ManyToMany(mappedBy = "students", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Set<Course> courses = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssignmentSubmission> submissions = new HashSet<>();

    @EqualsAndHashCode.Include
    @Override
    public long getUserID() {
        return super.getUserID();
    }
}
