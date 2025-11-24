package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_grades")
@Data
public class StudentGrade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="student_id")
    private Integer studentId;

    @Column(name="course_id")
    private Integer courseId;

    @Column(name="semester_id")
    private Integer semesterId;

    @Column(name="section_id")
    private Integer sectionId;

    @Column(name="grade_numeric")
    private Double gradeNumeric;

    @Column(name="grade_letter")
    private String gradeLetter;

    @Column(name="credits_awarded")
    private Double creditsAwarded;

    private String status;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}

