package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.dto.request.UpdateRoleRequest;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Admin vidi sve korisnike.
     * Primer: GET /api/users
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Admin menja rolu korisniku.
     */
    @PutMapping("/{userId}/role")
    public User updateUserRole(@PathVariable Long userId,
                               @Valid @RequestBody UpdateRoleRequest request) {
        return userService.updateUserRole(userId, request.getRole());
    }

    /**
     * Admin pretražuje korisnike po username-u.
     */
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String username) {
        return userService.searchUsersByUsername(username);
    }
}
