package com.eventhub.eventhub.service;

import com.eventhub.eventhub.exceptions.EventNotFoundException;
import com.eventhub.eventhub.exceptions.LocationNotFoundException;
import com.eventhub.eventhub.model.enums.Category;
import com.eventhub.eventhub.model.Event;
import com.eventhub.eventhub.model.Location;
import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.repository.CommentRepository;
import com.eventhub.eventhub.repository.EventRepository;
import com.eventhub.eventhub.repository.LocationRepository;
import com.eventhub.eventhub.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final ReservationRepository reservationRepository;
    private final CommentRepository commentRepository;

    public EventService(EventRepository eventRepository,
                        LocationRepository locationRepository,
                        ReservationRepository reservationRepository,
                        CommentRepository commentRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.reservationRepository = reservationRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Dodavanje novog događaja
     */
    public Event addEvent(Event event) {

        Long locationId = event.getLocation().getId();

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() ->
                        new LocationNotFoundException(
                                "Lokacija sa ovim id-em nije pronađena: " + locationId));

        event.setLocation(location);

        return eventRepository.save(event);
    }

    /**
     * Izmena postojećeg događaja
     */
    public Event updateEvent(Long id, Event updatedEvent) {

        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found with id: " + id));

        existingEvent.setEventName(updatedEvent.getEventName());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setDateTime(updatedEvent.getDateTime());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setOrganizer(updatedEvent.getOrganizer());
        existingEvent.setCategory(updatedEvent.getCategory());

        return eventRepository.save(existingEvent);
    }

    /**
     * Brisanje događaja
     */
    @Transactional
    public void deleteEvent(Long id) {

        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException("Event sa ovim id-em nije pronađen: " + id);
        }

        reservationRepository.deleteByEventId(id);
        commentRepository.deleteByEventId(id);

        eventRepository.deleteById(id);
    }

    /**
     * Vraća sve događaje
     */
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Pronalazak događaja po ID-u
     */
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found with id: " + id));
    }

    /**
     * Događaji koje je organizovao određeni korisnik
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer);
    }

    /**
     * Događaji na određenoj lokaciji
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByLocation(Location location) {
        return eventRepository.findByLocation(location);
    }

    /**
     * Događaji po kategoriji
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByCategory(Category category) {
        return eventRepository.findByCategory(category);
    }

    /**
     * Pretraga po nazivu
     */
    @Transactional(readOnly = true)
    public List<Event> searchByName(String keyword) {
        return eventRepository.findByEventNameContainingIgnoreCase(keyword);
    }

}

