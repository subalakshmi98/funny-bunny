package com.scheduling.maplewood.Dto;

import lombok.Data;

@Data
public class EnrollmentRequest {
    private Integer studentId;
    private Integer courseId;
    private Integer semesterId;
}