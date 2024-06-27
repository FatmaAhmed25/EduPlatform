package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    @JsonIgnore
    private Course course;

    @JsonBackReference
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "InstructorID", nullable = false)
    private Instructor instructor;

    @JsonManagedReference
    @OneToMany(mappedBy = "announcement", cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private List<Comment> comments;
}
