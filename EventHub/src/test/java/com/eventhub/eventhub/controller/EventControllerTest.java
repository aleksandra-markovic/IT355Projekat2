package com.eventhub.eventhub.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integracioni testovi za EventController.
 */

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@Rollback
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Testira da endpoint za prikaz svih događaja radi.
     */
    @Test
    void getAllEvents_shouldReturn200() throws Exception {

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk());
    }

    /**
     * Testira slučaj kada događaj sa datim ID-em ne postoji.
     */
    @Test
    void getEventById_whenEventDoesNotExist_shouldReturn404() throws Exception {

        mockMvc.perform(get("/api/events/99999"))
                .andExpect(status().isNotFound());
    }
}
