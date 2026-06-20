package com.eventhub.eventhub.service;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.model.enums.Role;
import com.eventhub.eventhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("marko");
        user.setEmail("marko@gmail.com");
        user.setPassword("123456");
    }

    @Test
    void registerUser_success() {

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.registerUser(user);

        assertEquals("marko", saved.getUsername());
        assertEquals("encodedPass", saved.getPassword());
        assertEquals(Role.ROLE_USER, saved.getRole());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void registerUser_shouldThrow_whenUsernameExists() {

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.registerUser(user));

        assertEquals("Username već postoji u bazi", ex.getMessage());
    }

    @Test
    void getByUsername_returnsUser() {

        when(userRepository.findByUsername("marko"))
                .thenReturn(Optional.of(user));

        User result = userService.getByUsername("marko");

        assertEquals("marko", result.getUsername());
    }
}
