package com.edu.eduplatform.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "Dtype")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private long userID;

    //TODO: nkhliha first name and last name
    @Column(name = "Name")
    private String username;

//    @Column(name = "FirstName")
//    private String firstName;
//
//    @Column(name = "LastName")
//    private String lastName;

    @Column(name = "Email", unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "Password")
    private String password;

    @Column(name = "Bio")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "UserType")
    private UserType userType;

    public enum UserType {
        ROLE_ADMIN,
        ROLE_INSTRUCTOR,
        ROLE_STUDENT
    }
}