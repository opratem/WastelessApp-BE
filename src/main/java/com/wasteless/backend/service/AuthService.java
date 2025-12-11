package com.wasteless.backend.service;

import com.wasteless.backend.config.JwtUtil;
import com.wasteless.backend.dto.AuthResponse;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Email already exists");
            errorResponse.put("error", "Registration failed");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());
        AuthResponse response = new AuthResponse(token, savedUser.getId(), savedUser.getEmail(),
                savedUser.getFullName(), savedUser.getPhone(), savedUser.getProfilePicture(), savedUser.isOnboardingCompleted());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail());
            AuthResponse response = new AuthResponse(token, user.getId(), user.getEmail(),
                    user.getFullName(), user.getPhone(), user.getProfilePicture(), user.isOnboardingCompleted());

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid email or password");
            errorResponse.put("error", "Authentication failed");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    public ResponseEntity<?> changePassword(String email, String currentPassword, String newPassword) {
        try {
            // Verify current password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, currentPassword)
            );

            // Get user and update password
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Password changed successfully");
            return ResponseEntity.ok().body(successResponse);

        } catch (AuthenticationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Current password is incorrect");
            errorResponse.put("error", "Authentication failed");
            return ResponseEntity.status(401).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to change password");
            errorResponse.put("error", "Server error");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
