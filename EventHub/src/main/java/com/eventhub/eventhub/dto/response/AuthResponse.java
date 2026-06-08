package com.eventhub.eventhub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO koji se vraća nakon uspešnog login-a.
 * Sadrži JWT token i osnovne informacije o korisniku.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String role;
}
