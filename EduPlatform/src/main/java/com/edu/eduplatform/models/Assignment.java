package com.edu.eduplatform.models;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@DiscriminatorValue("assignment")
public class Assignment extends Announcement {
    @Column(name = "DueDate")
    private LocalDateTime dueDate=LocalDateTime.now().plusDays(7);;


    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<AssignmentSubmission> submissions;

}