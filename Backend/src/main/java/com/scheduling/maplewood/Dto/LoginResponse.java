package com.scheduling.maplewood.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private int userId;
    private String role;
    private String name;
    private String email;
}
