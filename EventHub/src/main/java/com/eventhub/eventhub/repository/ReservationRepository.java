package com.eventhub.eventhub.repository;

import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.Reservation;
import com.eventhub.eventhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByEvent(Event event);

    Optional<Reservation> findByUserAndEvent(User user, Event event);

    boolean existsByUserAndEvent(User user, Event event);
}
