package com.eventhub.eventhub.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.eventhub.eventhub.model.Reservation;
import com.eventhub.eventhub.model.User;

@Repository
public interface ReservationRepository {
    
    List<Reservation> findByUser(User user);
}
