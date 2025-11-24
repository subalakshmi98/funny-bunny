package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="student_enrollments")
@Data
public class StudentEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="student_id")
    private Integer studentId;

    @Column(name="course_id")
    private Integer courseId;

    @Column(name="semester_id")
    private Integer semesterId;

    @Column(name="section_id")
    private Integer sectionId;
}
