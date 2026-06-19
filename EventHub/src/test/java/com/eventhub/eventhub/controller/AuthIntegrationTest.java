package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.dto.request.LoginRequest;
import com.eventhub.eventhub.dto.request.RegisterRequest;
import com.eventhub.eventhub.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.findByUsername("user_test")
                .ifPresent(userRepository::delete);

        userRepository.findByUsername("login_test")
                .ifPresent(userRepository::delete);
    }

    @AfterEach
    void cleanUp() {
        userRepository.findByUsername("user_test")
                .ifPresent(userRepository::delete);

        userRepository.findByUsername("login_test")
                .ifPresent(userRepository::delete);
    }

    @Test
    void register_shouldCreateUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user_test");
        request.setEmail("user_test@test.com");
        request.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user_test"))
                .andExpect(jsonPath("$.email").value("user_test@test.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("login_test");
        registerRequest.setEmail("login_test@test.com");
        registerRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("login_test");
        loginRequest.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("login_test"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }
}
