package com.eventhub.eventhub.service;
import com.eventhub.eventhub.exceptions.EmailAlreadyExistsException;
import com.eventhub.eventhub.exceptions.UserNotFoundException;
import com.eventhub.eventhub.exceptions.UsernameAlreadyExistsException;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.model.enums.Role;
import com.eventhub.eventhub.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registracija novog korisnika
     */
    public User registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Username već postoji u bazi");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email već postoji u bazi");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }

        return userRepository.save(user);
    }

    /**
     * Pronalazak korisnika po ID-u
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User sa ovim id-em nije pronađen: " + id));
    }

    /**
     * Pronalazak po username-u (bitno za JWT login)
     */
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "User sa ovim username-om nije pronađen: " + username));
    }

    /**
     * Vraća sve korisnike (admin funkcija)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    /**
     * Brisanje korisnika (admin funkcija)
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User sa ovim id-em nije pronađen: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public User updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));

        user.setRole(role);

        return userRepository.save(user);
    }
}
