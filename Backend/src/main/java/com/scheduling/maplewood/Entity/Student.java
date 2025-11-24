package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="students")
@Data
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private String email;

    @Column(name="grade_level")
    private Integer gradeLevel;

    @Column(name="enrollment_year")
    private Integer enrollmentYear;

    @Column(name="expected_graduation_year")
    private Integer expectedGraduationYear;

    private String status;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}

