package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.dto.request.LoginRequest;
import com.eventhub.eventhub.dto.request.RegisterRequest;
import com.eventhub.eventhub.dto.response.AuthResponse;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.model.enums.Role;
import com.eventhub.eventhub.security.JwtUtil;
import com.eventhub.eventhub.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * REGISTRACIJA KORISNIKA
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        // default role
        user.setRole(Role.ROLE_USER);

        User savedUser = userService.registerUser(user);

        return ResponseEntity.ok(savedUser);
    }

    /**
     * LOGIN + JWT GENERISANJE
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        User user = userService.getByUsername(request.getUsername());

        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        // provera lozinke (BCrypt)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        // generisanje JWT tokena
        String token = jwtUtil.generateToken(user.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(
                        token,
                        user.getUsername(),
                        user.getRole().name()
                )
        );
    }
}
