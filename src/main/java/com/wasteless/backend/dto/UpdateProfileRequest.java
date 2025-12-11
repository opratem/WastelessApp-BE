package com.wasteless.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String phone;
    private String profilePicture; // Base64 encoded image
}
