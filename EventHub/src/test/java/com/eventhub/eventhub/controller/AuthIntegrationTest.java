package com.eventhub.eventhub.controller;


import com.eventhub.eventhub.dto.request.LoginRequest;
import com.eventhub.eventhub.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * REGISTRATION + LOGIN FLOW TEST
     */
    @Test
    void register_and_login_flow_should_return_jwt() throws Exception {

        // REGISTER
        String registerJson = """
        {
            "username":"testuser1",
            "email":"test1@test.com",
            "password":"123456"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        // LOGIN
        String loginJson = """
        {
            "username":"testuser1",
            "password":"123456"
        }
        """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(response.contains("token"));
    }

    /**
     * PROTECTED ENDPOINT WITHOUT TOKEN
     */
    @Test
    void protected_endpoint_should_return_401_or_403() throws Exception {

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isForbidden());
    }

    /**
     * LOGIN WITH WRONG PASSWORD
     */
    @Test
    void login_with_wrong_password_should_return_401() throws Exception {

        // prvo kreiramo user
        String registerJson = """
        {
            "username":"wrongpassuser",
            "email":"wrong@test.com",
            "password":"123456"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        // pogrešan login
        String loginJson = """
        {
            "username":"wrongpassuser",
            "password":"wrongpass"
        }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    /**
     * DUPLICATE USERNAME TEST
     */
    @Test
    void register_duplicate_username_should_fail() throws Exception {

        String registerJson = """
        {
            "username":"duplicateuser",
            "email":"dup@test.com",
            "password":"123456"
        }
        """;

        // prvi put OK
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        // drugi put → CONFLICT (409)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isConflict());
    }
}
