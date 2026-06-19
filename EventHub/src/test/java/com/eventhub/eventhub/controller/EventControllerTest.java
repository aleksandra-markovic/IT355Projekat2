package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.dto.request.LoginRequest;
import com.eventhub.eventhub.model.*;
import com.eventhub.eventhub.model.enums.Category;
import com.eventhub.eventhub.model.enums.Role;
import com.eventhub.eventhub.repository.EventRepository;
import com.eventhub.eventhub.repository.LocationRepository;
import com.eventhub.eventhub.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User admin;
    private Location location;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        admin = new User();
        admin.setUsername("admin_test");
        admin.setEmail("admin_test@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin = userRepository.save(admin);

        location = new Location();
        location.setCity("Beograd");
        location.setAdress("Bulevar 1");
        location.setLocationName("Arena");
        location = locationRepository.save(location);

        adminToken = loginAndGetToken("admin_test", "admin123");
    }

    @AfterEach
    void cleanUp() {
        eventRepository.findByEventNameContainingIgnoreCase("Koncert test")
                .forEach(eventRepository::delete);

        if (location != null && location.getId() != null) {
            locationRepository.findById(location.getId())
                    .ifPresent(locationRepository::delete);
        }

        userRepository.findByUsername("admin_test")
                .ifPresent(userRepository::delete);
    }

    @Test
    void getAllEvents_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void admin_shouldCreateEvent() throws Exception {
        Event event = new Event();
        event.setEventName("Koncert test");
        event.setDescription("Opis koncerta za testiranje");
        event.setDateTime(LocalDateTime.of(2026, 7, 1, 20, 0));
        event.setCategory(Category.KONCERT);
        event.setLocation(location);
        event.setOrganizer(admin);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Koncert test"))
                .andExpect(jsonPath("$.category").value("KONCERT"));
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}
