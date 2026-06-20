package com.eventhub.eventhub.service;

import com.eventhub.eventhub.exceptions.EventNotFoundException;
import com.eventhub.eventhub.exceptions.ReservationAlreadyExistsException;
import com.eventhub.eventhub.exceptions.ReservationNotFoundException;
import com.eventhub.eventhub.exceptions.UserNotFoundException;
import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.Reservation;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.repository.EventRepository;
import com.eventhub.eventhub.repository.ReservationRepository;
import com.eventhub.eventhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /**
     * Korisnik se prijavljuje na event.
     */
    public Reservation createReservation(Long eventId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found: " + username));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found with id: " + eventId));

        if (reservationRepository.existsByUserAndEvent(user, event)) {
            throw new ReservationAlreadyExistsException(
                    "User already has reservation for this event");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setReservationDate(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    /**
     * Vraća sve prijave trenutno ulogovanog korisnika.
     */
    @Transactional(readOnly = true)
    public List<Reservation> getMyReservations(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found: " + username));

        return reservationRepository.findByUser(user);
    }

    /**
     * Korisnik briše svoju prijavu.
     */
    public void cancelReservation(Long reservationId, String username) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new ReservationNotFoundException(
                                "Reservation not found with id: " + reservationId));

        if (!reservation.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can delete only your own reservation");
        }

        reservationRepository.delete(reservation);
    }

    /**
     * Vraća sve prijave za određeni event.
     * Ovo može koristiti admin ili kasnije statistika.
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found with id: " + eventId));

        return reservationRepository.findByEvent(event);
    }
}
