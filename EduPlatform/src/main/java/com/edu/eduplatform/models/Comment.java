package com.edu.eduplatform.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Content", nullable = false)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "AnnouncementID", nullable = false)
    @JsonIgnore
    private Announcement announcement;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    @JsonIgnore
    private User user;
}
