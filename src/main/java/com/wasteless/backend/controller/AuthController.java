package com.wasteless.backend.controller;

import com.wasteless.backend.dto.AuthResponse;
import com.wasteless.backend.dto.ChangePasswordRequest;
import com.wasteless.backend.dto.UpdateProfileRequest;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.UserRepository;
import com.wasteless.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return authService.login(user.getEmail(), user.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value="Authorization", required = false) String token){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()){
            String email = authentication.getName();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("phone", user.getPhone());
        response.put("profilePicture", user.getProfilePicture());
        response.put("role", user.getRole());
        response.put("onboardingCompleted", user.isOnboardingCompleted());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user fields
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }

        // Save updated user
        userRepository.save(user);

        // Return updated user data
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("phone", user.getPhone());
        response.put("profilePicture", user.getProfilePicture());
        response.put("role", user.getRole());
        response.put("onboardingCompleted", user.isOnboardingCompleted());
        response.put("message", "Profile updated successfully");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return authService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());
    }

    @PutMapping("/complete-onboarding")
    public ResponseEntity<?> completeOnboarding() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setOnboardingCompleted(true);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Onboarding completed successfully");
        response.put("onboardingCompleted", true);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/onboarding-preferences")
    public ResponseEntity<?> saveOnboardingPreferences(@RequestBody Map<String, Object> preferences) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update profile picture if provided
        if (preferences.containsKey("profilePicture") && preferences.get("profilePicture") != null) {
            user.setProfilePicture((String) preferences.get("profilePicture"));
        }

        // Update username/fullName if provided
        if (preferences.containsKey("username") && preferences.get("username") != null) {
            user.setFullName((String) preferences.get("username"));
        }

        // Save the updated user
        userRepository.save(user);

        // Return success response with preferences (we can expand this later for storing locations/reminders)
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Preferences saved successfully");
        response.put("userId", user.getId());
        response.put("fullName", user.getFullName());
        response.put("profilePicture", user.getProfilePicture());
        response.put("preferences", preferences);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return user settings (can be expanded later with dedicated settings table)
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("onboardingCompleted", user.isOnboardingCompleted());

        // Default notification settings (can be stored in DB later)
        Map<String, Object> notificationSettings = new HashMap<>();
        notificationSettings.put("oneDayBefore", true);
        notificationSettings.put("threeDaysBefore", true);
        notificationSettings.put("onExpiry", false);
        notificationSettings.put("emailNotifications", true);
        notificationSettings.put("pushNotifications", true);
        response.put("notificationSettings", notificationSettings);

        // Default storage locations
        Map<String, Boolean> storageLocations = new HashMap<>();
        storageLocations.put("fridge", true);
        storageLocations.put("pantry", true);
        storageLocations.put("freezer", false);
        response.put("storageLocations", storageLocations);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/notification-settings")
    public ResponseEntity<?> updateNotificationSettings(@RequestBody Map<String, Object> settings) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // For now, just acknowledge the settings (can be stored in DB later)
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Notification settings updated successfully");
        response.put("settings", settings);

        return ResponseEntity.ok(response);
    }
}
