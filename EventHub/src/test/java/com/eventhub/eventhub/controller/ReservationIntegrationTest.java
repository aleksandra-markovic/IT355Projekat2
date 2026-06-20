package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.dto.request.LoginRequest;
import com.eventhub.eventhub.model.*;
import com.eventhub.eventhub.model.enums.Category;
import com.eventhub.eventhub.model.enums.Role;
import com.eventhub.eventhub.repository.EventRepository;
import com.eventhub.eventhub.repository.LocationRepository;
import com.eventhub.eventhub.repository.ReservationRepository;
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
class ReservationIntegrationTest {

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
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private User admin;
    private Event event;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {

        admin = userRepository.findByUsername("admin_res_test")
                .orElseGet(() -> {
                    User newAdmin = new User();
                    newAdmin.setUsername("admin_res_test");
                    newAdmin.setEmail("admin_res@test.com");
                    newAdmin.setPassword(passwordEncoder.encode("admin123"));
                    newAdmin.setRole(Role.ROLE_ADMIN);
                    return userRepository.save(newAdmin);
                });

        user = userRepository.findByUsername("user_res_test")
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername("user_res_test");
                    newUser.setEmail("user_res@test.com");
                    newUser.setPassword(passwordEncoder.encode("user123"));
                    newUser.setRole(Role.ROLE_USER);
                    return userRepository.save(newUser);
                });

        Location location = new Location();
        location.setCity("Novi Sad");
        location.setAdress("Adresa 1");
        location.setLocationName("SPENS");
        location = locationRepository.save(location);

        event = new Event();
        event.setEventName("Sportski događaj");
        event.setDescription("Opis sportskog događaja za test");
        event.setDateTime(LocalDateTime.now().plusDays(3));
        event.setCategory(Category.SPORT);
        event.setLocation(location);
        event.setOrganizer(admin);
        event = eventRepository.save(event);

        userToken = loginAndGetToken("user_res_test", "user123");
    }

    @Test
    void user_shouldCreateReservation() throws Exception {
        mockMvc.perform(post("/api/reservations/event/" + event.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event.id").value(event.getId()))
                .andExpect(jsonPath("$.user.username").value("user_res_test"));
    }

    @Test
    void user_shouldSeeMyReservations() throws Exception {
        mockMvc.perform(post("/api/reservations/event/" + event.getId())
                .header("Authorization", "Bearer " + userToken));

        mockMvc.perform(get("/api/reservations/my")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].event.eventName").value("Sportski događaj"));
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
