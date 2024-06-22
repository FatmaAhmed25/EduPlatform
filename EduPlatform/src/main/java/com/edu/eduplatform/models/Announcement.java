package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Content", nullable = false)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "FileName")
    private String fileName; // Optional

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "CourseID", nullable = false)
    private Course course;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "InstructorID", nullable = false)
    private Instructor instructor;
}
