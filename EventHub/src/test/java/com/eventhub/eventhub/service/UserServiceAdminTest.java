package com.eventhub.eventhub.service;

import com.eventhub.eventhub.exceptions.UserNotFoundException;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.model.enums.Role;
import com.eventhub.eventhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceAdminTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@gmail.com");
        user.setPassword("123456");
        user.setRole(Role.ROLE_USER);

        admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword("123456");
        admin.setRole(Role.ROLE_ADMIN);
    }

    @Test
    void getAllUsers_shouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user, admin));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("admin", result.get(1).getUsername());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void searchUsersByUsername_shouldReturnMatchingUsers() {
        when(userRepository.findByUsernameContainingIgnoreCase("test"))
                .thenReturn(List.of(user));

        List<User> result = userService.searchUsersByUsername("test");

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());

        verify(userRepository, times(1))
                .findByUsernameContainingIgnoreCase("test");
    }

    @Test
    void updateUserRole_shouldUpdateRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUserRole(1L, Role.ROLE_ADMIN);

        assertEquals(Role.ROLE_ADMIN, result.getRole());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserRole_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUserRole(1L, Role.ROLE_ADMIN)
        );

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}
