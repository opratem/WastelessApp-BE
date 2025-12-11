package com.wasteless.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String profilePicture;
    private boolean onboardingCompleted;

    public AuthResponse (String token) {
        this.token = token;
    }

}
