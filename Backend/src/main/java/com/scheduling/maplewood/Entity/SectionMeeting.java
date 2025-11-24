package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="section_meetings")
@Data
public class SectionMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="section_id")
    private Integer sectionId;

    @Column(name="day_of_week")
    private String dayOfWeek;

    @Column(name="start_time")
    private String startTime; // HH:mm

    @Column(name="end_time")
    private String endTime;   // HH:mm
}
