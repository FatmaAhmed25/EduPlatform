package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@DiscriminatorValue("assignment")
public class Assignment extends Announcement {
    @Column(name = "DueDate")
    private LocalDateTime dueDate = LocalDateTime.now().plusDays(7);

    @Column(name = "AllowLateSubmissions", nullable = false)
    private boolean allowLateSubmissions=true;

    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AssignmentSubmission> submissions;

}
