package com.edu.eduplatform.models;


import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseCode")
    private Long courseCode;

    @Column(name = "Title")
    private String title;

    @Column(name = "Description")
    private String description;

    @Column(name = "Password", nullable = false)
    private String password;

    // One-to-many relationship for the course creator
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Instructor createdBy;

    // Many-to-many relationship for TAs
    @ManyToMany
    @JoinTable(
            name = "course_instructors",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructor> taInstrucstors = new HashSet<>();

    public void setInstructor(Instructor instructor) {
        this.createdBy = instructor;
    }
}
