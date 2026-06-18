package com.eventhub.eventhub.controller;

import com.eventhub.eventhub.model.Reservation;
import com.eventhub.eventhub.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Korisnik se prijavljuje na event.
     * Primer: POST /api/reservations/event/1
     */
    @PostMapping("/event/{eventId}")
    public Reservation createReservation(@PathVariable Long eventId,
                                         Principal principal) {
        return reservationService.createReservation(eventId, principal.getName());
    }

    /**
     * Korisnik vidi svoje prijave.
     * Primer: GET /api/reservations/my
     */
    @GetMapping("/my")
    public List<Reservation> getMyReservations(Principal principal) {
        return reservationService.getMyReservations(principal.getName());
    }

    /**
     * Korisnik otkazuje svoju prijavu.
     * Primer: DELETE /api/reservations/5
     */
    @DeleteMapping("/{reservationId}")
    public void cancelReservation(@PathVariable Long reservationId,
                                  Principal principal) {
        reservationService.cancelReservation(reservationId, principal.getName());
    }

    /**
     * Pregled prijava za određeni event.
     * Kasnije može biti admin funkcionalnost.
     */
    @GetMapping("/event/{eventId}")
    public List<Reservation> getReservationsByEvent(@PathVariable Long eventId) {
        return reservationService.getReservationsByEvent(eventId);
    }
}
