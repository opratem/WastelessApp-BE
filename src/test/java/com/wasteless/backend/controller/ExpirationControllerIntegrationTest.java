package com.wasteless.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasteless.backend.config.TestSecurityConfig;
import com.wasteless.backend.dto.expiration.ExpirationSettingsRequest;
import com.wasteless.backend.dto.expiration.ExpirationSettingsResponse;
import com.wasteless.backend.dto.inventory.InventoryRequest;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.UserRepository;
import com.wasteless.backend.service.InventoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AlertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryService inventoryService;

    private static Long testUserId;

    @BeforeAll
    static void setup(@Autowired UserRepository userRepository, @Autowired InventoryService inventoryService) {
        // Create test user
        User user = new User();
        user.setFullName("Alert Test User");
        user.setEmail("alert_test@example.com");
        user.setPassword("password123");
        userRepository.save(user);
        testUserId = user.getId();

        // Add test inventory items with different expiry dates
        addTestItem(inventoryService, testUserId, "Milk", 1, "Dairy", "Fridge"); // expires in 1 day
        addTestItem(inventoryService, testUserId, "Bread", 3, "Bakery", "Pantry"); // expires in 3 days
        addTestItem(inventoryService, testUserId, "Yogurt", 5, "Dairy", "Fridge"); // expires in 5 days
        addTestItem(inventoryService, testUserId, "Cheese", 10, "Dairy", "Fridge"); // expires in 10 days
    }

    private static void addTestItem(InventoryService service, Long userId, String name, int daysUntilExpiry, String category, String location) {
        InventoryRequest request = InventoryRequest.builder()
                .name(name)
                .quantity(1)
                .category(category)
                .purchaseDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(daysUntilExpiry))
                .storageLocation(location)
                .build();
        service.addItem(userId, request);
    }

    @Test
    @Order(1)
    void testGetUpcomingExpirations() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/upcoming")
                        .param("userId", testUserId.toString())
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].daysUntilExpiry").exists())
                .andExpect(jsonPath("$[0].urgencyLevel").exists());
    }

    @Test
    @Order(2)
    void testGetCriticalExpirations() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/critical")
                        .param("userId", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(3)
    void testGetExpirationsToday() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/today")
                        .param("userId", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(4)
    void testGetAlertSettings() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/settings")
                        .param("userId", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.daysBeforeExpiryFirstAlert").value(3))
                .andExpect(jsonPath("$.daysBeforeExpirySecondAlert").value(1))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(true))
                .andExpect(jsonPath("$.pushNotificationsEnabled").value(true));
    }

    @Test
    @Order(5)
    void testUpdateAlertSettings() throws Exception {
        ExpirationSettingsRequest request = ExpirationSettingsRequest.builder()
                .daysBeforeExpiryFirstAlert(5)
                .daysBeforeExpirySecondAlert(2)
                .emailNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .alertOnExpiryDay(true)
                .build();

        mockMvc.perform(put("/api/v1/alerts/settings")
                        .param("userId", testUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daysBeforeExpiryFirstAlert").value(5))
                .andExpect(jsonPath("$.daysBeforeExpirySecondAlert").value(2))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(false))
                .andExpect(jsonPath("$.pushNotificationsEnabled").value(true));
    }

    @Test
    @Order(6)
    void testGetItemsRequiringAlerts() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/notifications")
                        .param("userId", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
