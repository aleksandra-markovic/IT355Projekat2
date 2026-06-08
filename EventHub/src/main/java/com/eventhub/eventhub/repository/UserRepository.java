package com.eventhub.eventhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.model.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
   
}
