package com.eventhub.eventhub.config;

import com.eventhub.eventhub.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Glavna Security konfiguracija
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    // admin može da dodaje, menja i briše evente
                    .requestMatchers(HttpMethod.POST, "/api/events/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("ADMIN")

                    // user i admin mogu da čitaju evente
                    .requestMatchers(HttpMethod.GET, "/api/events/**").hasAnyRole("USER", "ADMIN")

                    //Rezervacije
                    .requestMatchers(HttpMethod.POST, "/api/reservations/event/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/reservations/*").hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/reservations/*").hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/reservations/event/**").hasRole("ADMIN")

                    //Komentari
                    .requestMatchers(HttpMethod.GET, "/api/comments/event/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/comments/event/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasAnyRole("USER", "ADMIN")

                    // Users - samo admin
                    .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/users/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager (potreban za login + JWT generisanje)
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
