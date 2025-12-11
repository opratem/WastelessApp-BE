package com.wasteless.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasteless.backend.dto.impact.UpdateItemStatusRequest;
import com.wasteless.backend.model.InventoryItem;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.InventoryRepository;
import com.wasteless.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ImpactControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private InventoryItem testItem;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .email("impact@test.com")
                .password(passwordEncoder.encode("password"))
                .fullName("Impact Test User")
                .role("USER")
                .build();
        testUser = userRepository.save(testUser);

        // Set up security context with authenticated user
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create test inventory item
        testItem = InventoryItem.builder()
                .name("Test Tomatoes")
                .quantity(5)
                .category("Vegetables")
                .purchaseDate(LocalDate.now().minusDays(3))
                .expiryDate(LocalDate.now().plusDays(4))
                .storageLocation("Fridge")
                .status(InventoryItem.ItemStatus.ACTIVE)
                .estimatedValue(1000.0)
                .user(testUser)
                .build();
        testItem = inventoryRepository.save(testItem);
    }

    @Test
    void testGetCurrentMonthSummary() throws Exception {
        mockMvc.perform(get("/api/v1/impact/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").exists()) // Period format: "November 2025"
                .andExpect(jsonPath("$.moneySaved").exists())
                .andExpect(jsonPath("$.co2Saved").exists())
                .andExpect(jsonPath("$.itemsSaved").exists())
                .andExpect(jsonPath("$.itemsWasted").exists())
                .andExpect(jsonPath("$.wasteReductionPercentage").exists());
    }

    @Test
    void testGetLast30DaysSummary() throws Exception {
        mockMvc.perform(get("/api/v1/impact/summary/30days")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.period").value("Last 30 Days"))
                .andExpect(jsonPath("$.moneySaved").exists())
                .andExpect(jsonPath("$.co2Saved").exists());
    }

    @Test
    void testGetImpactHistory() throws Exception {
        mockMvc.perform(get("/api/v1/impact/history")
                        .param("months", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyData").isArray())
                .andExpect(jsonPath("$.totalImpact").exists())
                .andExpect(jsonPath("$.totalImpact.period").value("Last 3 Months"));
    }

    @Test
    void testUpdateItemStatusToEaten() throws Exception {
        UpdateItemStatusRequest request = new UpdateItemStatusRequest();
        request.setStatus(InventoryItem.ItemStatus.EATEN);
        request.setEstimatedValue(1200.0);

        mockMvc.perform(put("/api/v1/impact/items/" + testItem.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EATEN"))
                .andExpect(jsonPath("$.consumedAt").exists())
                .andExpect(jsonPath("$.estimatedValue").value(1200.0));
    }

    @Test
    void testUpdateItemStatusToWasted() throws Exception {
        UpdateItemStatusRequest request = new UpdateItemStatusRequest();
        request.setStatus(InventoryItem.ItemStatus.WASTED);

        mockMvc.perform(put("/api/v1/impact/items/" + testItem.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WASTED"))
                .andExpect(jsonPath("$.consumedAt").exists());
    }

    @Test
    void testImpactCalculationAfterMarkingItemsAsEaten() throws Exception {
        // Mark item as eaten
        UpdateItemStatusRequest request = new UpdateItemStatusRequest();
        request.setStatus(InventoryItem.ItemStatus.EATEN);
        request.setEstimatedValue(1000.0);

        mockMvc.perform(put("/api/v1/impact/items/" + testItem.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify impact summary reflects the saved item
        mockMvc.perform(get("/api/v1/impact/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemsSaved").value(1))
                .andExpect(jsonPath("$.itemsWasted").value(0))
                .andExpect(jsonPath("$.moneySaved").value(5000.0)) // 1000 * 5 items
                .andExpect(jsonPath("$.wasteReductionPercentage").value(100.0));
    }
}
