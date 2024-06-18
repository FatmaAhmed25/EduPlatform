package com.edu.eduplatform.models;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends User {
}