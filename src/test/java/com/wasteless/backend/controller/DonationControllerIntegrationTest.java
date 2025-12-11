package com.wasteless.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wasteless.backend.model.DonationCenter;
import com.wasteless.backend.model.User;
import com.wasteless.backend.repository.DonationCenterRepository;
import com.wasteless.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DonationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationCenterRepository donationCenterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private DonationCenter testCenter;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .email("donation@test.com")
                .password(passwordEncoder.encode("password"))
                .fullName("Donation Test User")
                .role("USER")
                .build();
        testUser = userRepository.save(testUser);

        // Create test donation center in Lagos, Nigeria
        testCenter = DonationCenter.builder()
                .name("Lagos Food Bank")
                .type("Food Bank")
                .address("123 Lagos Street, Victoria Island")
                .city("Lagos")
                .state("Lagos State")
                .latitude(6.4281)  // Victoria Island, Lagos coordinates
                .longitude(3.4219)
                .phoneNumber("+234-123-456-7890")
                .email("contact@lagosfoodbank.org")
                .openingHours("Mon-Fri: 9AM-5PM, Sat: 10AM-2PM")
                .acceptedItems("Fresh produce, canned goods, packaged foods")
                .isActive(true)
                .website("https://lagosfoodbank.org")
                .build();
        testCenter = donationCenterRepository.save(testCenter);

        // Create another center for testing
        DonationCenter shelter = DonationCenter.builder()
                .name("Hope Shelter Lagos")
                .type("Shelter")
                .address("456 Ikeja Road")
                .city("Lagos")
                .state("Lagos State")
                .latitude(6.6018)  // Ikeja, Lagos
                .longitude(3.3515)
                .phoneNumber("+234-987-654-3210")
                .email("info@hopeshelter.org")
                .openingHours("24/7")
                .acceptedItems("All non-perishable items, toiletries")
                .isActive(true)
                .build();
        donationCenterRepository.save(shelter);
    }

    @Test
    void testGetNearbyDonationCenters() throws Exception {
        // User location: Victoria Island, Lagos (close to test center)
        mockMvc.perform(get("/api/v1/donations/nearby")
                        .param("latitude", "6.4281")
                        .param("longitude", "3.4219")
                        .param("radius", "20.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].distanceKm").exists())
                .andExpect(jsonPath("$[0].latitude").exists())
                .andExpect(jsonPath("$[0].longitude").exists());
    }

    @Test
    void testGetNearbyDonationCentersWithTypeFilter() throws Exception {
        mockMvc.perform(get("/api/v1/donations/nearby")
                        .param("latitude", "6.4281")
                        .param("longitude", "3.4219")
                        .param("radius", "20.0")
                        .param("type", "Food Bank")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].type").value("Food Bank"));
    }

    @Test
    void testGetDonationCentersByCity() throws Exception {
        mockMvc.perform(get("/api/v1/donations/city/Lagos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)); // We created 2 centers in Lagos
    }

    @Test
    void testGetAllDonationCenters() throws Exception {
        mockMvc.perform(get("/api/v1/donations/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetDonationCenterById() throws Exception {
        mockMvc.perform(get("/api/v1/donations/" + testCenter.getId())
                        .param("latitude", "6.4281")
                        .param("longitude", "3.4219")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCenter.getId()))
                .andExpect(jsonPath("$.name").value("Lagos Food Bank"))
                .andExpect(jsonPath("$.type").value("Food Bank"))
                .andExpect(jsonPath("$.city").value("Lagos"))
                .andExpect(jsonPath("$.distanceKm").exists());
    }

    @Test
    void testCreateDonationCenter() throws Exception {
        DonationCenter newCenter = DonationCenter.builder()
                .name("Abuja Community Fridge")
                .type("Community Fridge")
                .address("789 Abuja Street")
                .city("Abuja")
                .state("FCT")
                .latitude(9.0765)
                .longitude(7.3986)
                .phoneNumber("+234-111-222-3333")
                .email("info@abujafridge.org")
                .openingHours("Always Open")
                .acceptedItems("Perishable and non-perishable items")
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/donations/centers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCenter)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Abuja Community Fridge"))
                .andExpect(jsonPath("$.type").value("Community Fridge"));
    }

    @Test
    void testNearbySearchWithLargeRadius() throws Exception {
        // Test with large radius to ensure we get results
        mockMvc.perform(get("/api/v1/donations/nearby")
                        .param("latitude", "6.5000")
                        .param("longitude", "3.3500")
                        .param("radius", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
