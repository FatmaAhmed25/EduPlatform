package com.edu.eduplatform.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)

public class Course {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseID")
    private Long courseId;


    @Column(name = "CourseCode" , unique = true)
    private String courseCode;

    @Column(name = "Title")
    private String title;

    @Column(name = "Description")
    private String description;

    @Column(name = "Password", nullable = false)
    private String password;

    // One-to-many relationship for the course creator
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false )
    private Instructor createdBy;

    // Many-to-many relationship for TAs
    @ManyToMany
    @JoinTable(
            name = "course_instructors",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructor> taInstructors = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "Course_Student",
            joinColumns = @JoinColumn(name = "CourseID"),
            inverseJoinColumns = @JoinColumn(name = "StudentID")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Student> students = new HashSet<>();

    public void setInstructor(Instructor instructor) {
        this.createdBy = instructor;
    }

}
