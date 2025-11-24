package com.scheduling.maplewood.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EligibleSectionResponse {

    private boolean success;
    private String message;
    private List<?> sections;
}
