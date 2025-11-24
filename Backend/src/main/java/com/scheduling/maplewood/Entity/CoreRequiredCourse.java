package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="core_required_courses")
@Data
public class CoreRequiredCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="course_id")
    private Integer courseId;
}
