package com.edu.eduplatform.models;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CourseID")
    private Long courseId;

    @Column(name = "Title")
    private String title;

    @Column(name = "Description")
    private String description;

    @Column(name = "Code")
    private String code;


    // TODO: mmken nkhleha many to many lw 3yzen msln el TAs zyhom zy el instructor aw n3mel user type gded lel TAs w nkhly el instructor y3ml assign lel Tas lel course
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;



}
