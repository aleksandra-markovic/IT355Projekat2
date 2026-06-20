package com.eventhub.eventhub.service;

import com.eventhub.eventhub.exceptions.ReservationAlreadyExistsException;
import com.eventhub.eventhub.exceptions.UserNotFoundException;
import com.eventhub.eventhub.exceptions.EventNotFoundException;
import com.eventhub.eventhub.exceptions.ReservationNotFoundException;
import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.Reservation;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.repository.EventRepository;
import com.eventhub.eventhub.repository.ReservationRepository;
import com.eventhub.eventhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Event event;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        event = new Event();
        event.setId(1L);
        event.setEventName("Utakmica");

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setEvent(event);
    }

    @Test
    void createReservation_shouldSaveReservation() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(reservationRepository.existsByUserAndEvent(user, event))
                .thenReturn(false);

        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(reservation);

        Reservation result = reservationService.createReservation(1L, "testuser");

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(event, result.getEvent());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(eventRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).existsByUserAndEvent(user, event);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                reservationService.createReservation(1L, "testuser")
        );

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(eventRepository, never()).findById(anyLong());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldThrowException_whenEventNotFound() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () ->
                reservationService.createReservation(1L, "testuser")
        );

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(eventRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldThrowException_whenReservationAlreadyExists() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(reservationRepository.existsByUserAndEvent(user, event))
                .thenReturn(true);

        assertThrows(ReservationAlreadyExistsException.class, () ->
                reservationService.createReservation(1L, "testuser")
        );

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void getMyReservations_shouldReturnUserReservations() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(reservationRepository.findByUser(user))
                .thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getMyReservations("testuser");

        assertEquals(1, result.size());
        assertEquals(user, result.get(0).getUser());
        assertEquals(event, result.get(0).getEvent());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(reservationRepository, times(1)).findByUser(user);
    }

    @Test
    void getMyReservations_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                reservationService.getMyReservations("testuser")
        );

        verify(reservationRepository, never()).findByUser(any(User.class));
    }

    @Test
    void cancelReservation_shouldDeleteReservation() {
        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L, "testuser");

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).delete(reservation);
    }

    @Test
    void cancelReservation_shouldThrowException_whenReservationNotFound() {
        when(reservationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ReservationNotFoundException.class, () ->
                reservationService.cancelReservation(1L, "testuser")
        );

        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, never()).delete(any(Reservation.class));
    }

    @Test
    void cancelReservation_shouldThrowException_whenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        reservation.setUser(anotherUser);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                reservationService.cancelReservation(1L, "testuser")
        );

        assertEquals("You can delete only your own reservation", exception.getMessage());

        verify(reservationRepository, never()).delete(any(Reservation.class));
    }

    @Test
    void getReservationsByEvent_shouldReturnReservations() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(reservationRepository.findByEvent(event))
                .thenReturn(List.of(reservation));

        List<Reservation> result = reservationService.getReservationsByEvent(1L);

        assertEquals(1, result.size());
        assertEquals(event, result.get(0).getEvent());

        verify(eventRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).findByEvent(event);
    }

    @Test
    void getReservationsByEvent_shouldThrowException_whenEventNotFound() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () ->
                reservationService.getReservationsByEvent(1L)
        );

        verify(reservationRepository, never()).findByEvent(any(Event.class));
    }
}
