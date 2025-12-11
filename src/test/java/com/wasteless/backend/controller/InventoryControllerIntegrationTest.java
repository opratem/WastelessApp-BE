package com.wasteless.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasteless.backend.config.TestSecurityConfig;
import com.wasteless.backend.dto.inventory.InventoryRequest;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static Long testUserId;
    private static Long savedItemId;

    @BeforeAll
    static void setup(@Autowired UserRepository userRepository) {
        User user = new User();
        user.setFullName("Inventory Test User");
        user.setEmail("inventory_test@example.com");
        user.setPassword("password123");
        userRepository.save(user);
        testUserId = user.getId();
    }

    @Test
    @Order(1)
    void testAddItem() throws Exception {
        InventoryRequest request = new InventoryRequest();
        request.setName("Milk");
        request.setQuantity(2);
        request.setCategory("Dairy");
        request.setPurchaseDate(LocalDate.now());
        request.setExpiryDate(LocalDate.now().plusDays(5));
        request.setStorageLocation("Fridge");

        String response = mockMvc.perform(post("/api/v1/inventory/{userId}/add", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milk"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // extract ID from response for later tests
        savedItemId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @Order(2)
    void testGetAllItems() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{userId}/all", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Milk"));
    }

    @Test
    @Order(3)
    void testGetItemById() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/item/{id}", savedItemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milk"));
    }

    @Test
    @Order(4)
    void testUpdateItem() throws Exception {
        InventoryRequest updated = new InventoryRequest();
        updated.setName("Chocolate Milk");
        updated.setQuantity(3);
        updated.setCategory("Dairy");
        updated.setPurchaseDate(LocalDate.now());
        updated.setExpiryDate(LocalDate.now().plusDays(7));
        updated.setStorageLocation("Fridge");

        mockMvc.perform(put("/api/v1/inventory/update/{id}", savedItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chocolate Milk"));
    }

    @Test
    @Order(5)
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/api/v1/inventory/delete/{id}", savedItemId))
                .andExpect(status().isNoContent());
    }
}
