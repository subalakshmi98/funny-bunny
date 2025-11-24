package com.scheduling.maplewood.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnrollmentResponse {
    private boolean success;
    private String message;
}
